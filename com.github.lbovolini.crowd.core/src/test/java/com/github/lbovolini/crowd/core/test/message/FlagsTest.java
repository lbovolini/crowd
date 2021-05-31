package com.github.lbovolini.crowd.core.test.message;

import com.github.lbovolini.crowd.core.message.Flags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlagsTest {

    private final Flags flags = new Flags();

    @Test
    void shouldResetAllFlags() {
        // Should test ONLY this method
        flags.resetAll();

        // Assertions
        assertFalse(flags.hasType());
        assertFalse(flags.hasSize());
        assertFalse(flags.hasSizeChunk());
        assertFalse(flags.hasMessage());
        assertFalse(flags.hasMessageChunk());
    }

}