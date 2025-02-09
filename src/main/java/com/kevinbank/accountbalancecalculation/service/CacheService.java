package com.kevinbank.accountbalancecalculation.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    void set(String key, Object value, long timeout, TimeUnit unit);
    <T> T get(String key, Class<T> type);
    void delete(String key);
    boolean exists(String key);
} 