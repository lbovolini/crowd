package com.github.lbovolini.crowd.group;

public interface MulticastService {

    /**
     * Será invocado após a inicialização do canal multicast.
     */
    default void onInit() {};

    /**
     * Será invocado após receber qualquer mensagem de qualquer host.
     * É invocado antes do handler.
     */
    default void onReceive() {};
}
