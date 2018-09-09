package com.github.lbovolini.crowd.common.group;


import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.object.Caller;

import com.github.lbovolini.crowd.common.connection.Connection;

public interface Group extends Caller {

    void join(HostDetails nodeInfo, Connection channel);

    void leave(String hostAddress);
}