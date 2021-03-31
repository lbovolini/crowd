package com.github.lbovolini.crowd.classloader.service;

import java.net.URL;

public interface RemoteClassLoader {

    void addURLs(URL[] urls);

    void addLibURL(URL url);
}
