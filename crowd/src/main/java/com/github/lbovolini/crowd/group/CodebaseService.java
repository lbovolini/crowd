package com.github.lbovolini.crowd.group;

import java.net.InetSocketAddress;
import java.net.URL;

public interface CodebaseService {

    void onConnect(URL[] codebase, URL libURL, InetSocketAddress serverAddress);

    void onUpdate(URL[] codebase, URL libURL);

    void onReload(URL[] codebase, URL libURL);
}
