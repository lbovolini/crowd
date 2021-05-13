package com.github.lbovolini.crowd.example.oneway.master;

import com.github.lbovolini.crowd.example.oneway.OneWay;
import com.github.lbovolini.crowd.example.oneway.RemoteOneWay;
import com.github.lbovolini.crowd.node.Crowd;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {
        Crowd<RemoteOneWay> crowd = new Crowd<>(OneWay.class.getName());
        crowd.forOne(oneWay -> oneWay.say(1));
    }
}
