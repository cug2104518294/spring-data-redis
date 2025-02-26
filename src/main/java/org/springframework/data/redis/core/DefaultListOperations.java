package org.springframework.data.redis.core;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisListCommands.Position;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

class DefaultListOperations<K, V> extends AbstractOperations<K, V> implements ListOperations<K, V> {

    DefaultListOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public V index(K key, long index) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.lIndex(rawKey, index);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPop(java.lang.Object)
     */
    @Override
    public V leftPop(K key) {

        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.lPop(rawKey);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPop(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public V leftPop(K key, long timeout, TimeUnit unit) {

        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);
        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> lPop = connection.bLPop(tm, rawKey);
                return (CollectionUtils.isEmpty(lPop) ? null : lPop.get(1));
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPush(java.lang.Object, java.lang.Object)
     */
    @Override
    public Long leftPush(K key, V value) {

        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.lPush(rawKey, rawValue), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPushAll(java.lang.Object, java.lang.Object[])
     */
    @Override
    public Long leftPushAll(K key, V... values) {

        byte[] rawKey = rawKey(key);
        byte[][] rawValues = rawValues(values);
        return execute(connection -> connection.lPush(rawKey, rawValues), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPushAll(java.lang.Object, java.util.Collection)
     */
    @Override
    public Long leftPushAll(K key, Collection<V> values) {

        byte[] rawKey = rawKey(key);
        byte[][] rawValues = rawValues(values);

        return execute(connection -> connection.lPush(rawKey, rawValues), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#leftPushIfPresent(java.lang.Object, java.lang.Object)
     */
    @Override
    public Long leftPushIfPresent(K key, V value) {

        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.lPushX(rawKey, rawValue), true);
    }


    @Override
    public Long leftPush(K key, V pivot, V value) {
        byte[] rawKey = rawKey(key);
        byte[] rawPivot = rawValue(pivot);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.lInsert(rawKey, Position.BEFORE, rawPivot, rawValue), true);
    }

    @Override
    public Long size(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.lLen(rawKey), true);
    }

    @Override
    public List<V> range(K key, long start, long end) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> deserializeValues(connection.lRange(rawKey, start, end)), true);
    }

    @Override
    public Long remove(K key, long count, Object value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.lRem(rawKey, count, rawValue), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPop(java.lang.Object)
     */
    @Override
    public V rightPop(K key) {

        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.rPop(rawKey);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPop(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public V rightPop(K key, long timeout, TimeUnit unit) {

        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);

        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> bRPop = connection.bRPop(tm, rawKey);
                return (CollectionUtils.isEmpty(bRPop) ? null : bRPop.get(1));
            }
        }, true);
    }

    @Override
    public Long rightPush(K key, V value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.rPush(rawKey, rawValue), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPushAll(java.lang.Object, java.lang.Object[])
     */
    @Override
    public Long rightPushAll(K key, V... values) {

        byte[] rawKey = rawKey(key);
        byte[][] rawValues = rawValues(values);
        return execute(connection -> connection.rPush(rawKey, rawValues), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPushAll(java.lang.Object, java.util.Collection)
     */
    @Override
    public Long rightPushAll(K key, Collection<V> values) {

        byte[] rawKey = rawKey(key);
        byte[][] rawValues = rawValues(values);
        return execute(connection -> connection.rPush(rawKey, rawValues), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPushIfPresent(java.lang.Object, java.lang.Object)
     */
    @Override
    public Long rightPushIfPresent(K key, V value) {

        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.rPushX(rawKey, rawValue), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPush(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public Long rightPush(K key, V pivot, V value) {

        byte[] rawKey = rawKey(key);
        byte[] rawPivot = rawValue(pivot);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.lInsert(rawKey, Position.AFTER, rawPivot, rawValue), true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPopAndLeftPush(java.lang.Object, java.lang.Object)
     */
    @Override
    public V rightPopAndLeftPush(K sourceKey, K destinationKey) {

        byte[] rawDestKey = rawKey(destinationKey);
        return execute(new ValueDeserializingRedisCallback(sourceKey) {

            @Override
            protected byte[] inRedis(byte[] rawSourceKey, RedisConnection connection) {
                return connection.rPopLPush(rawSourceKey, rawDestKey);
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#rightPopAndLeftPush(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit) {

        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);
        byte[] rawDestKey = rawKey(destinationKey);
        return execute(new ValueDeserializingRedisCallback(sourceKey) {

            @Override
            protected byte[] inRedis(byte[] rawSourceKey, RedisConnection connection) {
                return connection.bRPopLPush(tm, rawSourceKey, rawDestKey);
            }
        }, true);
    }

    @Override
    public void set(K key, long index, V value) {

        byte[] rawValue = rawValue(value);
        execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                connection.lSet(rawKey, index, rawValue);
                return null;
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.core.ListOperations#trim(java.lang.Object, long, long)
     */
    @Override
    public void trim(K key, long start, long end) {

        execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                connection.lTrim(rawKey, start, end);
                return null;
            }
        }, true);
    }
}
