package com.github.lbovolini.crowd.core.test.request;

import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    @Mock
    private RequestHandler requestHandler;

    @InjectMocks
    private Scheduler scheduler;

    @Test
    void shouldAddRequestToQueue() {
        // Input
        Request request = new Request(null, null);

        // Should test ONLY this method
        boolean success = scheduler.enqueue(request);

        // Assertion
        assertTrue(success);
    }
}