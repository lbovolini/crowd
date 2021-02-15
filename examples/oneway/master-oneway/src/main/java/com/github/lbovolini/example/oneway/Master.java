package com.github.lbovolini.example.oneway;

import com.github.lbovolini.crowd.node.Crowd;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {
        Crowd<RemoteOneWay> crowd = new Crowd<>(OneWay.class.getName());
        crowd.forOne(oneWay -> oneWay.say(1));
    }
}
