/**
 * 缓存服务接口，提供了缓存操作的基本方法
 * 用于管理应用中的缓存数据，包括设置缓存、获取缓存、删除缓存和检查缓存是否存在
 */
package com.kevinbank.accountbalancecalculation.service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口定义
 * 提供了对缓存进行操作的方法，包括设置、获取、删除缓存项以及检查缓存项是否存在
 */
public interface CacheService {

    /**
     * 设置缓存
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> type);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 检查缓存是否存在
     */
    boolean exists(String key);
}
