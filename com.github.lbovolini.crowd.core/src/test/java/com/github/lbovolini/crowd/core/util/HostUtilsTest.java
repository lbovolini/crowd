package com.github.lbovolini.crowd.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HostUtilsTest {

    @Test
    void shouldReturnNetworkInterfaceName() {
        String name = HostUtils.getNetworkInterfaceName();

        assertTrue(name.length() > 0);
    }

    @Test
    void shouldReturnHostAddressName() {
        String name = HostUtils.getHostAddressName();

        assertTrue(name.length() > 0);
    }
}