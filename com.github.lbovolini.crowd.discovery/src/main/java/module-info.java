module com.github.lbovolini.crowd.discovery {

    exports com.github.lbovolini.crowd.discovery.service;

    exports com.github.lbovolini.crowd.discovery.worker;

    exports com.github.lbovolini.crowd.discovery.message to
            com.github.lbovolini.crowd.client,
            com.github.lbovolini.crowd.server,
            com.github.lbovolini.crowd.discovery.test,
            org.mockito;

    exports com.github.lbovolini.crowd.discovery.connection to
            com.github.lbovolini.crowd.client,
            com.github.lbovolini.crowd.server,
            com.github.lbovolini.crowd.discovery.test,
            org.mockito;

    exports com.github.lbovolini.crowd.discovery.monitor to
            com.github.lbovolini.crowd.server,
            com.github.lbovolini.crowd.discovery.test,
            org.mockito;

    exports com.github.lbovolini.crowd.discovery.request;

    exports com.github.lbovolini.crowd.discovery.exception;

    exports com.github.lbovolini.crowd.discovery.util to
            com.github.lbovolini.crowd.discovery.test,
            org.mockito;
}