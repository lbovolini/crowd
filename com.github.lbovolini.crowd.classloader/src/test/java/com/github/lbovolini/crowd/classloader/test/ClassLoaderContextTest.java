package com.github.lbovolini.crowd.classloader.test;

import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.*;

class ClassLoaderContextTest {

    private final String classPath = "";
    private final String libPath = "";

    private final ClassLoaderContext classLoaderContext = new ClassLoaderContext(classPath, libPath);

    @Test
    void shouldReturnThreadFactoryWithCustomClassloader() {
        // Expected output
        ClassLoader expectedOutput = classLoaderContext.getCustomClassLoader();

        // Should test ONLY this method
        ThreadFactory threadFactory = classLoaderContext.getThreadFactory();

        Thread thread = threadFactory.newThread(() -> {});
        ClassLoader customClassLoader = thread.getContextClassLoader();

        // Assertions
        assertNotNull(customClassLoader);
        assertEquals(expectedOutput, customClassLoader);
    }

    @Test
    void shouldReturnClassPath() {
        // Expected output
        String expectedOutput = this.classPath;

        // Should test ONLY this method
        String classPath = classLoaderContext.getClassPath();

        // Assertions
        assertEquals(expectedOutput, classPath);
    }

    @Test
    void shouldReturnLibPath() {
        // Expected output
        String expectedOutput = this.libPath;

        // Should test ONLY this method
        String libPath = classLoaderContext.getLibPath();

        // Assertions
        assertEquals(expectedOutput, libPath);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenClassPathIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new ClassLoaderContext(null, "");
        });
    }

    @Test
    void shouldThrowNullPointerExceptionWhenLibPathIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new ClassLoaderContext("", null);
        });
    }

    @Test
    void shouldReturnNullClassURLs() {
        assertNull(classLoaderContext.getClassURLs());
    }

    @Test
    void shouldSetClassURLs() {
        try {
            // Input
            URL[] urlArray = new URL[] { new URL("http://localhost/") };

            // Should test ONLY this method
            classLoaderContext.setClassURLs(urlArray);

            // Assertions
            assertEquals(urlArray, classLoaderContext.getClassURLs());

        } catch (MalformedURLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void shouldAcceptNullClassURLs() {
        // Input
        URL[] urlArray = null;

        // Should test ONLY this method
        classLoaderContext.setClassURLs(urlArray);

        // Assertions
        assertNull(classLoaderContext.getClassURLs());
    }

    @Test
    void shouldReturnNullLibURL() {
        assertNull(classLoaderContext.getLibURL());
    }

    @Test
    void shouldSetLibURLs() {
        try {
            // Input
            URL libURL = new URL("http://localhost/");

            // Should test ONLY this method
            classLoaderContext.setLibURL(libURL);

            // Assertions
            assertEquals(libURL, classLoaderContext.getLibURL());

        } catch (MalformedURLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void shouldAcceptNullLibURL() {
        // Input
        URL libURL = null;

        // Should test ONLY this method
        classLoaderContext.setLibURL(libURL);

        // Assertions
        assertNull(classLoaderContext.getLibURL());
    }

    @Test
    void shouldReturnDefaultClassloader() {
        // Expected output
        ClassLoader expectedOutput = ClassLoaderContext.class.getClassLoader();

        // Should test ONLY this method
        ClassLoader defaultClassLoader = classLoaderContext.getDefaultClassLoader();

        // Assertions
        assertEquals(expectedOutput, defaultClassLoader);
    }

    @Test
    void shouldReturnCustomClassloader() {
        assertNotNull(classLoaderContext.getCustomClassLoader());
    }
}