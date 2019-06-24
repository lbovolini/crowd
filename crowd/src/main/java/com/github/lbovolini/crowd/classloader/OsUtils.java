package com.github.lbovolini.crowd.classloader;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class OsUtils {

    private static final String ARCH_x86    = "x86";
    private static final String ARCH_x86_64 = "x86_64";
    private static final String ARCH_ARM_32 = "arm32";
    private static final String ARCH_ARM_64 = "arm64";

    private static final String OS_WINDOWS = "windows";
    private static final String OS_LINUX   = "linux";
    private static final String OS_ANDROID = "android";

    public static String getArch() {

        if (OS_ARCH.contains("amd64") || OS_ARCH.contains("x86_64")) {
            return ARCH_x86_64;
        }
        if (OS_ARCH.contains("x86")) {
            return ARCH_x86;
        }
        if (OS_ARCH.contains("armeabi") || OS_ARCH.contains("armv7")) {
            return ARCH_ARM_32;
        }
        if (OS_ARCH.contains("arm64")) {
            return ARCH_ARM_64;
        }

        return OS_ARCH;
    }

    public static String getOs() {

        if (VENDOR.contains("Android")) {
            return OS_ANDROID;
        }
        if (OS_NAME.contains("Linux")) {
            return OS_LINUX;
        }
        if (OS_NAME.contains("Windows")) {
            return OS_WINDOWS;
        }

        return OS_NAME;
    }

}
