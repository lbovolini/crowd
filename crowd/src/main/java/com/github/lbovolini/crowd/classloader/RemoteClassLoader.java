package com.github.lbovolini.crowd.classloader;

import java.net.URL;

public interface RemoteClassLoader {

    void addURLs(URL[] urls);

    void addLibURL(URL url);
}
