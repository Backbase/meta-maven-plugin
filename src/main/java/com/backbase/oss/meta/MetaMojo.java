package com.backbase.oss.meta;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * Generate metadata artefacts from the configuration.
 */
@Mojo(name = "meta", requiresProject = true, defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class MetaMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;

    @Parameter
    private XmlPlexusConfiguration meta;

    /**
     * Specifies the formats of the metadata.
     * <p>
     * A format is specified by supplying one of the following values in a &lt;format&gt; subelement:
     * <ul>
     * <li><b>xml</b></li>
     * <li><b>json</b></li>
     * <li><b>yaml</b></li>
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
     * Whether to attach the metadata as artifacts.
     */
    @Parameter(property = "meta.attach", defaultValue = "true")
    private boolean attach;

    @Override
    public void execute() throws MojoExecutionException {
        this.formats.forEach(f -> generate(f, this.meta.toString()));
    }

    private void generate(String type, String xml) {
        final MetaFormat format = MetaFormat.valueOf(type.toUpperCase());
        final String fileName = this.project.getBuild().getFinalName()
            + ofNullable(this.classifier).map(c -> "-" + c).orElse("")
            + "." + type;
        final File artefact = new File(this.project.getBuild().getDirectory(), fileName);
        final String text = format.convert(xml);

        try {
            createMeta(artefact, type, text);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(fileName, e);
        }
    }

    public void createMeta(File artefact, String type, String text) throws FileNotFoundException {
        artefact.getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(artefact)) {
            out.write(text);
        }

        if (this.attach) {
            this.projectHelper.attachArtifact(this.project, type, this.classifier, artefact);
        }
    }
}
