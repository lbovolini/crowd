package com.github.lbovolini.crowd.example.oneway;

public class OneWay implements RemoteOneWay {

    public void say(int i) {
        System.out.println("Cliente recebeu " + i);
    }

}
