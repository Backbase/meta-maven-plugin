package com.backbase.oss.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MetaMojoTest {
    static final String JSON = ""
        + "{\"images\": [\n"
        + "    {\n"
        + "        \"registry\": \"experimental\",\n"
        + "        \"image\": \"${project.artifactId}\",\n"
        + "        \"tag\": \"${project.version}\",\n"
        + "        \"hello\": \"world\"\n"
        + "    },\n"
        + "    {\n"
        + "        \"registry\": \"staging\",\n"
        + "        \"image\": \"${project.artifactId}\",\n"
        + "        \"tag\": \"${project.version}-special\",\n"
        + "        \"hello\": \"dolly\",\n"
        + "        \"suffix\": \"special\"\n"
        + "    }\n"
        + "]}";

    static final String XML = "\n"
        + "<images>\n"
        + "    <registry>experimental</registry>\n"
        + "    <image>${project.artifactId}</image>\n"
        + "    <tag>${project.version}</tag>\n"
        + "    <hello>world</hello>\n"
        + "</images>\n"
        + "<images>\n"
        + "    <registry>staging</registry>\n"
        + "    <image>${project.artifactId}</image>\n"
        + "    <tag>${project.version}-special</tag>\n"
        + "    <suffix>special</suffix>\n"
        + "    <hello>dolly</hello>\n"
        + "</images>\n"
        + "";

    static final String YAML = ""
        + "images:\n"
        + "- registry: experimental\n"
        + "  image: ${project.artifactId}\n"
        + "  tag: ${project.version}\n"
        + "  hello: world\n"
        + "- registry: staging\n"
        + "  image: ${project.artifactId}\n"
        + "  tag: ${project.version}-special\n"
        + "  hello: dolly\n"
        + "  suffix: special";

    @Test
    void json() {
        final String expected = JSON.replaceAll("\\s+", " ");
        final String actual = MetaFormat.JSON.convert(XML).replaceAll("\\s+", " ");

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertEquals(expected, actual);
    }

    @Test
    void xml() {
        final String expected = XML.replaceAll("\\s+", " ");
        final String actual = MetaFormat.XML.convert(XML).replaceAll("\\s+", " ");

        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);

        assertEquals(expected, actual);
    }

    @Test
    void yaml() {
        final String expected = YAML.trim();
        final String actual = MetaFormat.YAML.convert(XML).trim();

        System.out.println("expected:\n" + expected);
        System.out.println("actual:\n" + actual);

        assertEquals(expected, actual);
    }
}
