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
     * 设置缓存项
     *
     * @param key 缓存项的键，用于唯一标识缓存中的数据
     * @param value 缓存项的值，可以是任意对象类型
     * @param timeout 缓存项的过期时间
     * @param unit 过期时间的时间单位
     *
     * 将给定键和值的缓存项添加到缓存中，并设置缓存项的过期时间
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取缓存项
     *
     * @param key 缓存项的键
     * @param type 缓存项的类型，用于转换返回的对象类型
     * @param <T> 泛型参数，表示缓存项的类型
     * @return 返回指定类型的缓存项，如果缓存项不存在或类型不匹配，则返回null
     *
     * 根据键获取缓存项，并将缓存项转换为指定的类型返回
     */
    <T> T get(String key, Class<T> type);

    /**
     * 删除缓存项
     *
     * @param key 缓存项的键
     *
     * 从缓存中删除指定键的缓存项
     */
    void delete(String key);

    /**
     * 检查缓存项是否存在
     *
     * @param key 缓存项的键
     * @return 如果缓存中存在指定键的缓存项，则返回true；否则返回false
     *
     * 检查缓存中是否存在指定键的缓存项
     */
    boolean exists(String key);
}
