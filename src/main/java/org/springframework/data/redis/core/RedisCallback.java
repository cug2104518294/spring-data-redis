package org.springframework.data.redis.core;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.lang.Nullable;

/**
 * Callback interface for Redis 'low level' code. To be used with  RedisTemplate execution methods, often as
 * anonymous classes within a method implementation. Usually, used for chaining several operations together (
 * {@code get/set/trim etc...}.
 **/
public interface RedisCallback<T> {

    /**
     * Gets called by RedisTemplate with an active Redis connection. Does not need to care about activating or
     * closing the connection or handling exceptions.
     */
    @Nullable
    T doInRedis(RedisConnection connection) throws DataAccessException;
}
