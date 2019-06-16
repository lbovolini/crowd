package com.github.lbovolini.example.oneway;

public class OneWay implements RemoteOneWay {

    public void say(int i) {
        System.out.println("Cliente recebeu " + i);
    }

}
