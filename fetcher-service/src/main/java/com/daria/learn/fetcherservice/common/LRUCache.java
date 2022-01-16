package com.daria.learn.fetcherservice.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LRUCache {

    private final Map<String, LRUDeque<Object>> cacheMap;
    private int cacheSize = 100;
    private int queueSize = 200;

    public LRUCache() {
        cacheMap = new ConcurrentHashMap<>(cacheSize);
    }

    public LRUCache(int cacheSize, int queueSize) {
        this();
        this.cacheSize = cacheSize;
        this.queueSize = queueSize;
    }

    public void put(String key, Object value) {
        //TODO: concurrent?
        LRUDeque<Object> queue = cacheMap.getOrDefault(key, new LRUDeque<>(queueSize));
        queue.add(value);
        cacheMap.put(key, queue);
    }

    public boolean contains(String key, Object value) {
        return cacheMap.containsKey(key) && cacheMap.get(key).contains(value);
    }

}
