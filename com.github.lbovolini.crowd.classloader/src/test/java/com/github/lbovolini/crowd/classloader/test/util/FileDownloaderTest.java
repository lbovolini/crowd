package com.github.lbovolini.crowd.classloader.test.util;

import com.github.lbovolini.crowd.classloader.util.FileDownloader;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8888)
class FileDownloaderTest {

    @Test
    void shouldDownloadFile() throws Exception {
        var testFileName = "test.txt";
        var testFile = new File(getClass().getClassLoader().getResource("__files/" + testFileName).toURI());

        var expectedOutput = new File("/tmp/" + testFileName);
        expectedOutput.delete();

        try {
            stubFor(get("/file").willReturn(aResponse().withHeader("Content-Length", String.valueOf(testFile.length())).withBodyFile(testFileName)));

            var success = FileDownloader.download(URI.create("http://localhost:8888/file").toURL(), "/tmp/test.txt");

            assertTrue(success);
            assertTrue(expectedOutput.exists());
            assertEquals(testFile.length(), expectedOutput.length());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            expectedOutput.delete();
        }
    }

    @Test
    void shouldNotDownloadFileWithoutContentLength() {
        var testFileName = "test.txt";

        var expectedOutput = new File("/tmp/" + testFileName);
        expectedOutput.delete();

        try {
            stubFor(get("/file").willReturn(aResponse().withBodyFile(testFileName)));

            var success = FileDownloader.download(URI.create("http://localhost:8888/file").toURL(), "/tmp/test.txt");

            assertFalse(success);
            assertFalse(expectedOutput.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            expectedOutput.delete();
        }
    }
}