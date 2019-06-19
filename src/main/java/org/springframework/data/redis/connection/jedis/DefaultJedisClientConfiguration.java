package org.springframework.data.redis.connection.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.lang.Nullable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;
import java.util.Optional;

/**
 * Default implementation of JedisClientConfiguration.
 */
class DefaultJedisClientConfiguration implements JedisClientConfiguration {

    private final boolean useSsl;
    private final Optional<SSLSocketFactory> sslSocketFactory;
    private final Optional<SSLParameters> sslParameters;
    private final Optional<HostnameVerifier> hostnameVerifier;
    private final boolean usePooling;
    private final Optional<GenericObjectPoolConfig> poolConfig;
    private final Optional<String> clientName;
    private final Duration readTimeout;
    private final Duration connectTimeout;

    DefaultJedisClientConfiguration(boolean useSsl, @Nullable SSLSocketFactory sslSocketFactory,
                                    @Nullable SSLParameters sslParameters, @Nullable HostnameVerifier hostnameVerifier, boolean usePooling,
                                    @Nullable GenericObjectPoolConfig poolConfig, @Nullable String clientName, Duration readTimeout,
                                    Duration connectTimeout) {

        this.useSsl = useSsl;
        this.sslSocketFactory = Optional.ofNullable(sslSocketFactory);
        this.sslParameters = Optional.ofNullable(sslParameters);
        this.hostnameVerifier = Optional.ofNullable(hostnameVerifier);
        this.usePooling = usePooling;
        this.poolConfig = Optional.ofNullable(poolConfig);
        this.clientName = Optional.ofNullable(clientName);
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public boolean isUseSsl() {
        return useSsl;
    }

    @Override
    public Optional<SSLSocketFactory> getSslSocketFactory() {
        return sslSocketFactory;
    }

    @Override
    public Optional<SSLParameters> getSslParameters() {
        return sslParameters;
    }

    @Override
    public Optional<HostnameVerifier> getHostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public boolean isUsePooling() {
        return usePooling;
    }

    @Override
    public Optional<GenericObjectPoolConfig> getPoolConfig() {
        return poolConfig;
    }

    @Override
    public Optional<String> getClientName() {
        return clientName;
    }

    @Override
    public Duration getReadTimeout() {
        return readTimeout;
    }

    @Override
    public Duration getConnectTimeout() {
        return connectTimeout;
    }
}
