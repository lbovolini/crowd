module com.github.lbovolini.crowd.android {
    requires com.github.lbovolini.crowd.classloader;
    requires dalvik.dx;
    requires transitive android;

    exports com.github.lbovolini.crowd.android.classloader;
}