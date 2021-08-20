package com.backbase.oss.meta;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.json.JSONObject;

/**
 * Generate metadata artefacts from the configuration.
 */
@Mojo(name = "meta", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class MetaMojo extends AbstractMojo {

    @Component
    private MavenProject project;
    @Component
    private MavenSession session;
    @Component
    private MojoExecution execution;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * Free-form configuration in XML format used to generate the metadata.
     */
    @Parameter(required = true)
    private XmlPlexusConfiguration meta;

    /**
     * Specifies the formats of the metadata.
     * <p>
     * A format can be one of the following values added as a &lt;format&gt; subelement:
     * <ul>
     * <li><b>json</b></li>
     * <li><b>xml</b></li>
     * <li><b>yaml</b></li>
     * <li><b>yml</b></li>
     * </p>
     */
    @Parameter(property = "meta.formats", defaultValue = "json")
    private List<String> formats;

    /**
     * The classifier of the metadata artifact.
     */
    @Parameter(property = "meta.classifier", defaultValue = "meta")
    private String classifier;

    /**
     * Whether or not to attach the metadata as artifacts.
     */
    @Parameter(property = "meta.attach", defaultValue = "true")
    private boolean attach;

    /**
     * Whether or not to detect arrays and restructure the meta accordingly.
     * <p>
     * Usually, in the XML world, arrays are represented as nested elements of a base element, e.g.
     * <code>
     * <pre>
        &lt;items&gt;
            &lt;item&gt;value1&lt;/item&gt;
            &lt;item&gt;value2&lt;/item&gt;
        &lt;/items&gt;
     * </pre>
     * </code>
     *
     * By applying a raw translation to JSON, the sample above is transformed to <code>
     * <code>
     * <pre>
      "items":{
          "item": ["value1", "value2" ]
       }
     * </pre>
     * </code>
     *
     * ... which is not quite we want, i.e.
     *
     * <code>
     * <pre>
       {
          "items": ["value1", "value2" ]
       }
     * </pre>
     * </code>
     */
    @Parameter(property = "meta.prettyArrays", defaultValue = "true")
    private boolean prettyArrays;

    /**
     * Skip the execution for POM packaging.
     * <p>
     * By default, the execution is skipped for POM packaging, but setting this flag to {@code false}
     * will generate the meta artifact for a POM project too.
     * </p>
     * <p>
     * The developers that use this flag need to be aware that the configuration of a Maven plugin is
     * propagated to child modules, therefore they may need to
     * <ul>
     * <li>set &lt;inherited&gt; element to {@code false}</li>
     * <li>disable the plugin in the child modules</li>
     * <li>add child specific executions</li>
     * <ul>
     * </p>
     */
    @Parameter(property = "meta.skipPom", defaultValue = "true")
    private boolean skipPom;

    /**
     * Skip the execution.
     */
    @Parameter(property = "meta.skip")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException {
        if (this.skip) {
            getLog().info("execution is skipped");

            return;
        }
        if (this.skipPom && "pom".equals(this.project.getPackaging())) {
            getLog().info("execution is skipped for POM packaging");

            return;
        }

        final JSONObject json = new MetaConverter(this.session, this.execution)
            .prettyArrays(this.prettyArrays)
            .toJson(this.meta);

        for (final String format : this.formats) {
            generate(format, json);
        }
    }

    private void generate(String type, JSONObject json) {
        final MetaFormat format = MetaFormat.valueOf(type.toUpperCase());
        final String fileName = this.project.getBuild().getFinalName()
            + ofNullable(this.classifier).map(c -> "-" + c).orElse("")
            + "." + type;
        final File artefact = new File(this.project.getBuild().getDirectory(), fileName);
        final String text = format.convert(json);

        try {
            createMeta(artefact, type, text);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(fileName, e);
        }
    }

    private void createMeta(File file, String type, String text) throws FileNotFoundException {
        file.getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(file)) {
            out.write(text);
        }

        if (this.attach) {
            final Path path = this.project.getBasedir().getParentFile().toPath().relativize(file.toPath());

            getLog().info("Attached " + path);

            this.projectHelper.attachArtifact(this.project, type, this.classifier, file);
        }
    }
}
