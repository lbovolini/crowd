package com.github.lbovolini.crowd.discovery.test.message;

import com.github.lbovolini.crowd.discovery.exception.InvalidMulticastMessageException;
import com.github.lbovolini.crowd.discovery.exception.MalformedMulticastServerResponseException;
import com.github.lbovolini.crowd.discovery.message.ResponseFactory;
import com.github.lbovolini.crowd.discovery.message.ServerResponse;
import com.github.lbovolini.crowd.discovery.message.ServerResponseFactory;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ServerResponseTest {

    private static final ResponseFactory responseFactory = new ServerResponseFactory("localhost", 8888);

    @Test
    void shouldReturnObjectResponseFromResponseAsString() {
        // Input
        String codebase = "file:";
        String serverAddress = "localhost";
        String serverPort = "8888";
        String nativeLib = "file:";
        String type = "9"; // UPDATE

        StringBuilder builder = new StringBuilder();
        builder.append(codebase);
        builder.append(";");
        builder.append(serverAddress);
        builder.append(";");
        builder.append(serverPort);
        builder.append(";");
        builder.append(nativeLib);
        builder.append(";");
        builder.append(type);

        String response = builder.toString();

        // Should test ONLY this method
        ServerResponse serverResponse = ServerResponse.fromObject(response);

        // Assertions
        assertNotNull(serverResponse);
        try {
            assertArrayEquals(new URL[] { new URL(codebase) }, serverResponse.getCodebase());
            assertEquals(new URL(nativeLib), serverResponse.getLibURL());
        } catch (MalformedURLException e) {
            fail(e);
        }
        assertEquals(new InetSocketAddress(serverAddress, Integer.parseInt(serverPort)), serverResponse.getServerAddress());
        assertEquals(Byte.parseByte(type), serverResponse.getType());
    }

    @Test
    void shouldThrowsMalformedMulticastServerResponseExceptionWhenResponseStringIsMissingSomeOfExpectedFiveArguments() {
        // Input
        String response = "file:;localhost;8888;"; // 3

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowsMalformedMulticastServerResponseExceptionWhenResponseStringIsMissingSomeOfExpectedParameter() {
        // Input
        String response = "file:;localhost;8888;file:;"; // 4

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowsMalformedMulticastServerResponseExceptionWhenResponseStringHasSomeBlankParameter() {
        // Input
        String response = "file:;localhost;8888; ;9"; // 5, one blank

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidType() {
        // Input
        String response = "file:;localhost;8888;file:;0"; // 5, 0 is an invalid type

        // Should test ONLY this method
        assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidPortNumber() {
        // Input
        String response = "file:;localhost;1111111111;file:;9"; // 5

        // Should test ONLY this method
        assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidPortNumberIsZero() {
        // Input
        String response = "file:;localhost;0;file:;9"; // 5

        // Should test ONLY this method
        assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenSomeURLOfCodebaseIsMalformed() {
        // Input
        String response = "file: malformedURL?/;localhost;8888;file:;9"; // 5

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenURLOfNativeLibraryIsMalformed() {
        // Input
        String response = "file:;localhost;8888;malformedURL?/;9"; // 5

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenResponseIsNull() {
        // Input
        String response = null;

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenMessageTypeIsNotANumber() {
        // Input
        String response = "file:;localhost;8888;file:;abc"; // 5

        // Should test ONLY this method
        assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });
    }
    
}