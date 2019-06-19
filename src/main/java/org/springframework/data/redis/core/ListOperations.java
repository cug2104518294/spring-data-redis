package org.springframework.data.redis.core;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ListOperations<K, V> {

    @Nullable
    List<V> range(K key, long start, long end);

    void trim(K key, long start, long end);

    @Nullable
    Long size(K key);

    @Nullable
    Long leftPush(K key, V value);

    @Nullable
    Long leftPushAll(K key, V... values);

    @Nullable
    Long leftPushAll(K key, Collection<V> values);

    @Nullable
    Long leftPushIfPresent(K key, V value);

    @Nullable
    Long leftPush(K key, V pivot, V value);

    @Nullable
    Long rightPush(K key, V value);


    @Nullable
    Long rightPushAll(K key, V... values);

    @Nullable
    Long rightPushAll(K key, Collection<V> values);

    @Nullable
    Long rightPushIfPresent(K key, V value);

    @Nullable
    Long rightPush(K key, V pivot, V value);

    void set(K key, long index, V value);

    @Nullable
    Long remove(K key, long count, Object value);

    @Nullable
    V index(K key, long index);

    @Nullable
    V leftPop(K key);

    @Nullable
    V leftPop(K key, long timeout, TimeUnit unit);

    @Nullable
    V rightPop(K key);

    @Nullable
    V rightPop(K key, long timeout, TimeUnit unit);

    @Nullable
    V rightPopAndLeftPush(K sourceKey, K destinationKey);

    @Nullable
    V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);

    RedisOperations<K, V> getOperations();
}
