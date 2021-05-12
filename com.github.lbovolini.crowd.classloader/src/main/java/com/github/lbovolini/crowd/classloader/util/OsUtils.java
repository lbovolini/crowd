package com.github.lbovolini.crowd.classloader.util;

public class OsUtils {

    public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();
    public static final String VENDOR = System.getProperty("java.vendor").toLowerCase();
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    private static final String ARCH_x86    = "x86";
    private static final String ARCH_x86_64 = "x86_64";
    private static final String ARCH_ARM_32 = "armeabi-v7a";
    private static final String ARCH_ARM_64 = "arm64-v8a";

    private static final String OS_WINDOWS = "windows";
    private static final String OS_LINUX   = "linux";
    private static final String OS_ANDROID = "android";

    public static String getArch() {

        if (OS_ARCH.equals("amd64") || OS_ARCH.equals("x86_64") || OS_ARCH.equals("x86-64")) {
            return ARCH_x86_64;
        }
        if (OS_ARCH.equals("x86") || OS_ARCH.equals("x86_32") || OS_ARCH.equals("i686")) {
            return ARCH_x86;
        }
        if (OS_ARCH.equals("armeabi")) {
            return ARCH_ARM_32;
        }
        if (OS_ARCH.equals("aarch64")) {
            return ARCH_ARM_64;
        }

        return OS_ARCH;
    }

    public static String getOs() {

        if (VENDOR.contains("android")) {
            return OS_ANDROID;
        }
        if (OS_NAME.contains("linux")) {
            return OS_LINUX;
        }
        if (OS_NAME.contains("windows")) {
            return OS_WINDOWS;
        }

        return OS_NAME;
    }

}
