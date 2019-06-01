package com.github.lbovolini.crowd.time;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionTimeUtils {
    static Map<UUID, NodeTimeInfo> nodeTimes = new ConcurrentHashMap<>();

    public static void start(UUID id, int cores) {
        nodeTimes.put(id, new NodeTimeInfo(cores));
    }

    public static void end(UUID id) {
        NodeTimeInfo nodeTimeInfo = nodeTimes.get(id);
        nodeTimeInfo.finished();
    }

    public static void addComputationTime(UUID id, long computationTime) {
        NodeTimeInfo nodeTimeInfo = nodeTimes.get(id);
        nodeTimeInfo.addComputationTime(computationTime);
    }
}
