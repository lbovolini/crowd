package com.github.lbovolini.crowd.core.test.object;

import java.io.Serializable;

public interface Greetings<T> extends Serializable {

    void hello();

    T hi();
}
