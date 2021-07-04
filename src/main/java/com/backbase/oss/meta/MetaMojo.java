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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 * Generate metadata artefacts from the configuration.
 */
@Mojo(name = "meta", requiresProject = true, defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class MetaMojo extends AbstractMojo {

    static JSONObject toMeta(String xml) {
        return (JSONObject) ofNullable(XML.toJSONObject(xml).get("meta"))
            .orElseThrow(() -> new IllegalArgumentException("Cannot find any meta"));
    }

    static JSONObject prettyArrays(JSONObject json) {
        if (json.length() != 1) {
            return json;
        }

        final String key1 = json.keys().next();
        final Object val1 = json.get(key1);

        if (val1 instanceof JSONObject) {
            if ((json.length() == 1)) {
                final String key2 = ((JSONObject) val1).keys().next();
                final Object val2 = ((JSONObject) val1).get(key2);

                if (val2 instanceof JSONArray) {
                    json.put(key1, val2);
                }
            } else {
                prettyArrays((JSONObject) val1);
            }
        }

        return json;
    }

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
     * Usually, in the XML world, arrays are represented as nested elements of an base element, e.g.
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
        if (this.meta == null) {
            getLog().info("meta is null, execution skipped");

            return;
        }

        JSONObject json = toMeta(this.meta.toString());

        if (this.prettyArrays) {
            json = prettyArrays(json);
        }

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
