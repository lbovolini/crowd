open module com.github.lbovolini.crowd.classloader.test {
    requires org.mockito;
    requires net.bytebuddy;
    requires net.bytebuddy.agent;
    requires mockito.junit.jupiter;
    requires transitive org.junit.jupiter.engine;
    requires transitive org.junit.jupiter.api;
    requires com.github.lbovolini.crowd.classloader;
}