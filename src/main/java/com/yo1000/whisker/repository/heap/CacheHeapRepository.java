package com.yo1000.whisker.repository.heap;

import com.yo1000.whisker.repository.CacheRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yoichi.kikuchi on 2016/01/26.
 */
@Repository
public class CacheHeapRepository implements CacheRepository {
    private static final Map<String, Object> CACHE_MAP = new ConcurrentHashMap<>();

    public Object select(String key) {
        return CACHE_MAP.get(key);
    }

    public void insert(String key, Object value) {
        CACHE_MAP.put(key, value);
    }

    public void update(String key, Object value) {
        CACHE_MAP.put(key, value);
    }

    public boolean exists(String key) {
        return CACHE_MAP.containsKey(key);
    }
}
