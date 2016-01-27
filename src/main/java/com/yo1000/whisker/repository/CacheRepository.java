package com.yo1000.whisker.repository;

/**
 * Created by yoichi.kikuchi on 2016/01/26.
 */
public interface CacheRepository {
    Object select(String key);
    void insert(String key, Object value);
    void delete(String key);
    boolean exists(String key);
}
