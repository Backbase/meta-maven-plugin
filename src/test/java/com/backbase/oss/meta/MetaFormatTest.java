package com.backbase.oss.meta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetaFormatTest {

    @Mock(lenient = true)
    private ExpressionEvaluator expEval;
    private XmlPlexusConfiguration config;
    private MetaConverter conv;

    @BeforeEach
    void beforeEach() throws ExpressionEvaluationException, XmlPullParserException, IOException {
        reset(this.expEval);
        when(this.expEval.evaluate(anyString()))
            .then(iom -> iom.getArgument(0, String.class));

        this.conv = new MetaConverter(this.expEval);

        final Xpp3Dom dom = Xpp3DomBuilder.build(new StringReader(TestUtil.read("input.xml")));

        this.config = new XmlPlexusConfiguration(dom);
    }

    @Test
    void json() throws MojoExecutionException {
        final JSONObject json = this.conv.toJson(this.config);
        final String expected = TestUtil.toOneLine(TestUtil.read("expected.json"));
        final String actual = TestUtil.toOneLine(MetaFormat.JSON.convert(json));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void jsonPrettyArrays() throws MojoExecutionException {
        final JSONObject json = this.conv.prettyArrays(true).toJson(this.config);
        final String expected = TestUtil.toOneLine(TestUtil.read("expected-pretty-arrays.json"));
        final String actual = TestUtil.toOneLine(MetaFormat.JSON.convert(json));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void xml() throws MojoExecutionException {
        final JSONObject json = this.conv.toJson(this.config);
        final String expected = TestUtil.toOneLine(TestUtil.read("expected.xml"));
        final String actual = TestUtil.toOneLine(MetaFormat.XML.convert(json));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void xmlPrettyArrays() throws MojoExecutionException {
        final JSONObject json = this.conv.prettyArrays(true).toJson(this.config);
        final String expected = TestUtil.toOneLine(TestUtil.read("expected-pretty-arrays.xml"));
        final String actual = TestUtil.toOneLine(MetaFormat.XML.convert(json));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void yaml() throws MojoExecutionException {
        final JSONObject json = this.conv.toJson(this.config);
        final String expected = TestUtil.read("expected.yml").trim();
        final String actual = MetaFormat.YAML.convert(json).trim();

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void yamlPrettyArrays() throws MojoExecutionException {
        final JSONObject json = this.conv.prettyArrays(true).toJson(this.config);
        final String expected = TestUtil.read("expected-pretty-arrays.yml").trim();
        final String actual = MetaFormat.YAML.convert(json).trim();

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

}
