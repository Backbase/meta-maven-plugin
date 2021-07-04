package com.backbase.oss.meta;

import static com.backbase.oss.meta.MetaMojo.*;
import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.util.IOUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetaFormatTest {

    static String read(String resource) {
        try (InputStream is = currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            assertNotNull(is, "Cannot find resource " + resource);

            return IOUtil.toString(is);
        } catch (final IOException e) {
            fail("Cannot read resource " + resource);

            return null;
        }
    }

    static String oneLine(String text) {
        return text.replaceAll("\\s+", "");
    }

    private JSONObject input;

    @BeforeEach
    void setUp() {
        this.input = toMeta(read("input.xml"));
    }

    @Test
    void json() {
        final String expected = oneLine(read("expected.json"));
        final String actual = oneLine(MetaFormat.JSON.convert(this.input));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void jsonPrettyArrays() {
        this.input = prettyArrays(this.input);

        final String expected = oneLine(read("expected-pretty-arrays.json"));
        final String actual = oneLine(MetaFormat.JSON.convert(this.input));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void xml() {
        final String expected = oneLine(read("input.xml"));
        final String actual = oneLine(MetaFormat.XML.convert(this.input));

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void yaml() {
        final String expected = read("expected.yml").trim();
        final String actual = MetaFormat.YAML.convert(this.input).trim();

        System.out.println("expected:\n" + expected);
        System.out.println("actual:\n" + actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void yamlPrettyArrays() {
        this.input = prettyArrays(this.input);

        final String expected = read("expected-pretty-arrays.yml").trim();
        final String actual = MetaFormat.YAML.convert(this.input).trim();

        System.out.println("expected:\n" + expected);
        System.out.println("actual:\n" + actual);

        assertThat(actual).isEqualTo(expected);
    }
}
