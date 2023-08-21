package com.github.lbovolini.crowd.classloader.test;

import com.github.lbovolini.crowd.classloader.RemoteNativeLibrary;
import com.github.lbovolini.crowd.classloader.util.OsUtils;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@WireMockTest(httpPort = 8888)
class RemoteNativeLibraryTest {

    private RemoteNativeLibrary remoteNativeLibrary = new RemoteNativeLibrary(URI.create("http://localhost:8888/").toURL(), "/tmp/");

    RemoteNativeLibraryTest() throws MalformedURLException {
    }

    @Test
    void shouldDownloadNativeLibrary() throws Exception {

        var testFileName = "libhello.so";
        String os = OsUtils.getOs();
        String arch = OsUtils.getArch();

        var testFile = new File(getClass().getClassLoader().getResource("__files/linux/x86_64/" + testFileName).toURI());

        try {
            var urlPath = "/" + os + "/" + arch + "/" + testFileName;

            stubFor(get(urlPath).willReturn(aResponse().withHeader("Content-Length", String.valueOf(testFile.length())).withBodyFile(os + "/" + arch + "/" + testFileName)));

            var filePath = remoteNativeLibrary.download("hello");
            assertNotNull(filePath);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}