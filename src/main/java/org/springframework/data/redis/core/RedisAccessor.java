package org.springframework.data.redis.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Base class for {@link RedisTemplate} defining common properties. Not intended to be used directly.
 */
public class RedisAccessor implements InitializingBean {

    /**
     * Logger available to subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private @Nullable
    RedisConnectionFactory connectionFactory;

    @Override
    public void afterPropertiesSet() {
        Assert.state(getConnectionFactory() != null, "RedisConnectionFactory is required");
    }

    @Nullable
    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public RedisConnectionFactory getRequiredConnectionFactory() {
        RedisConnectionFactory connectionFactory = getConnectionFactory();
        if (connectionFactory == null) {
            throw new IllegalStateException("RedisConnectionFactory is required");
        }
        return connectionFactory;
    }

    /**
     * Sets the connection factory.
     */
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
