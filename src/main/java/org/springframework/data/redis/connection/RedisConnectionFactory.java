package org.springframework.data.redis.connection;

import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * Thread-safe factory of Redis connections.
 */
public interface RedisConnectionFactory extends PersistenceExceptionTranslator {

    /**
     * Provides a suitable connection for interacting with Redis.
     */
    RedisConnection getConnection();

    /**
     * Provides a suitable connection for interacting with Redis Cluster.
     */
    RedisClusterConnection getClusterConnection();

    boolean getConvertPipelineAndTxResults();

    /**
     * Provides a suitable connection for interacting with Redis Sentinel.
     */
    RedisSentinelConnection getSentinelConnection();
}
