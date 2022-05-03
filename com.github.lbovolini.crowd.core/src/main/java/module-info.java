module com.github.lbovolini.crowd.core {
    requires org.slf4j;

    exports com.github.lbovolini.crowd.core.connection;
    exports com.github.lbovolini.crowd.core.request;
    exports com.github.lbovolini.crowd.core.message;
    exports com.github.lbovolini.crowd.core.message.messages;
    exports com.github.lbovolini.crowd.core.node;
    exports com.github.lbovolini.crowd.core.util;
    exports com.github.lbovolini.crowd.core.worker;
    exports com.github.lbovolini.crowd.core.buffer;
    exports com.github.lbovolini.crowd.core.object
            to com.github.lbovolini.crowd.core.test;
}