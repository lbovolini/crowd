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
        // Expected exception message
        String expectedExceptionMessage = String.format("Malformed multicast server response. Response message must have exactly %d parameters", ServerResponse.PARAMETERS);

        // Input
        String response = "file:;localhost;8888;"; // 3

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowsMalformedMulticastServerResponseExceptionWhenResponseStringIsMissingSomeOfExpectedParameter() {
        // Expected exception message
        String expectedExceptionMessage = String.format("Malformed multicast server response. Response message must have exactly %d parameters", ServerResponse.PARAMETERS);

        // Input
        String response = "file:;localhost;8888;file:;"; // 4

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowsMalformedMulticastServerResponseExceptionWhenResponseStringHasSomeBlankParameter() {
        // Expected exception message
        String expectedExceptionMessage = "Malformed multicast server response. Some parameter is invalid";

        // Input
        String response = "file:;localhost;8888; ;9"; // 5, one blank

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidType() {
        // Expected exception message
        String expectedExceptionMessage = "Unknown multicast message type of type: 0 ";

        // Input
        String response = "file:;localhost;8888;file:;0"; // 5, 0 is an invalid type

        // Should test ONLY this method
        Throwable throwable = assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidPortNumber() {
        // Expected exception message
        String expectedExceptionMessage = "Invalid server address or port number";

        // Input
        String response = "file:;localhost;1111111111;file:;9"; // 5

        // Should test ONLY this method
        Throwable throwable = assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowsInvalidMulticastMessageExceptionWhenResponseStringHasInvalidPortNumberIsZero() {
        // Expected exception message
        String expectedExceptionMessage = "Invalid server address or port number";

        // Input
        String response = "file:;localhost;0;file:;9"; // 5

        // Should test ONLY this method
        Throwable throwable = assertThrows(InvalidMulticastMessageException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenSomeURLOfCodebaseIsMalformed() {
        // Expected exception message
        String expectedExceptionMessage = "java.net.MalformedURLException: no protocol: malformedCodebaseURL?/";

        // Input
        String response = "file: malformedCodebaseURL?/;localhost;8888;file:;9"; // 5

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenURLOfNativeLibraryIsMalformed() {
        // Expected exception message
        String expectedExceptionMessage = "java.net.MalformedURLException: no protocol: malformedURL?/";

        // Input
        String response = "file:;localhost;8888;malformedURL?/;9"; // 5

        // Should test ONLY this method
        Throwable throwable =assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenResponseIsNull() {
        // Expected exception message
        String expectedExceptionMessage = "Malformed multicast server response. Response message is null";

        // Input
        String response = null;

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }

    @Test
    void shouldThrowMalformedMulticastServerResponseExceptionWhenMessageTypeIsNotANumber() {
        // Expected exception message
        String expectedExceptionMessage = "java.lang.NumberFormatException: For input string: \"abc\"";

        // Input
        String response = "file:;localhost;8888;file:;abc"; // 5

        // Should test ONLY this method
        Throwable throwable = assertThrows(MalformedMulticastServerResponseException.class, () -> {
            ServerResponse.fromObject(response);
        });

        // Assert exception message
        assertEquals(expectedExceptionMessage, throwable.getMessage());
    }
    
}