package com.github.lbovolini.crowd.classloader.test;

import com.github.lbovolini.crowd.classloader.NativeLibrary;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class NativeLibraryTest {

    @Test
    void shouldCreateNativeLibrary() {
        NativeLibrary.create("/tmp/libtest.so");
    }

}