package com.backbase.oss.meta;

import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.util.IOUtil;

final class TestUtil {

    private TestUtil() {}

    static String read(String resource) {
        try (InputStream is = currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            assertNotNull(is, "Cannot find resource " + resource);

            return IOUtil.toString(is);
        } catch (final IOException e) {
            fail("Cannot read resource " + resource);

            return null;
        }
    }

    static String toOneLine(String text) {
        return text.replaceAll("\\s+", "");
    }

}
