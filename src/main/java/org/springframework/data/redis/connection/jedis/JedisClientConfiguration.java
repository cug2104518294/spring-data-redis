package org.springframework.data.redis.connection.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;
import java.util.Optional;

public interface JedisClientConfiguration {

    boolean isUseSsl();

    Optional<SSLSocketFactory> getSslSocketFactory();

    Optional<SSLParameters> getSslParameters();

    Optional<HostnameVerifier> getHostnameVerifier();

    boolean isUsePooling();

    Optional<GenericObjectPoolConfig> getPoolConfig();

    Optional<String> getClientName();

    Duration getConnectTimeout();

    Duration getReadTimeout();

    static JedisClientConfigurationBuilder builder() {
        return new DefaultJedisClientConfigurationBuilder();
    }

    static JedisClientConfiguration defaultConfiguration() {
        return builder().build();
    }


    interface JedisClientConfigurationBuilder {

        JedisSslClientConfigurationBuilder useSsl();

        JedisPoolingClientConfigurationBuilder usePooling();

        JedisClientConfigurationBuilder clientName(String clientName);

        JedisClientConfigurationBuilder readTimeout(Duration readTimeout);

        JedisClientConfigurationBuilder connectTimeout(Duration connectTimeout);

        JedisClientConfiguration build();
    }

    interface JedisPoolingClientConfigurationBuilder {

        JedisPoolingClientConfigurationBuilder poolConfig(GenericObjectPoolConfig poolConfig);

        JedisClientConfigurationBuilder and();

        JedisClientConfiguration build();
    }

    interface JedisSslClientConfigurationBuilder {

        JedisSslClientConfigurationBuilder sslSocketFactory(SSLSocketFactory sslSocketFactory);

        JedisSslClientConfigurationBuilder sslParameters(SSLParameters sslParameters);

        JedisSslClientConfigurationBuilder hostnameVerifier(HostnameVerifier hostnameVerifier);

        JedisClientConfigurationBuilder and();

        JedisClientConfiguration build();
    }

    class DefaultJedisClientConfigurationBuilder implements JedisClientConfigurationBuilder,
            JedisPoolingClientConfigurationBuilder, JedisSslClientConfigurationBuilder {

        private boolean useSsl;
        private @Nullable
        SSLSocketFactory sslSocketFactory;
        private @Nullable
        SSLParameters sslParameters;
        private @Nullable
        HostnameVerifier hostnameVerifier;
        private boolean usePooling;
        private GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
        private @Nullable
        String clientName;
        private Duration readTimeout = Duration.ofMillis(Protocol.DEFAULT_TIMEOUT);
        private Duration connectTimeout = Duration.ofMillis(Protocol.DEFAULT_TIMEOUT);

        private DefaultJedisClientConfigurationBuilder() {
        }

        @Override
        public JedisSslClientConfigurationBuilder useSsl() {

            this.useSsl = true;
            return this;
        }

        @Override
        public JedisSslClientConfigurationBuilder sslSocketFactory(SSLSocketFactory sslSocketFactory) {

            Assert.notNull(sslSocketFactory, "SSLSocketFactory must not be null!");

            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        @Override
        public JedisSslClientConfigurationBuilder sslParameters(SSLParameters sslParameters) {

            Assert.notNull(sslParameters, "SSLParameters must not be null!");

            this.sslParameters = sslParameters;
            return this;
        }

        @Override
        public JedisSslClientConfigurationBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {

            Assert.notNull(hostnameVerifier, "HostnameVerifier must not be null!");

            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        @Override
        public JedisPoolingClientConfigurationBuilder usePooling() {

            this.usePooling = true;
            return this;
        }

        @Override
        public JedisPoolingClientConfigurationBuilder poolConfig(GenericObjectPoolConfig poolConfig) {

            Assert.notNull(poolConfig, "GenericObjectPoolConfig must not be null!");

            this.poolConfig = poolConfig;
            return this;
        }

        @Override
        public JedisClientConfigurationBuilder and() {
            return this;
        }

        @Override
        public JedisClientConfigurationBuilder clientName(String clientName) {
            Assert.hasText(clientName, "Client name must not be null or empty!");
            this.clientName = clientName;
            return this;
        }

        @Override
        public JedisClientConfigurationBuilder readTimeout(Duration readTimeout) {
            Assert.notNull(readTimeout, "Duration must not be null!");
            this.readTimeout = readTimeout;
            return this;
        }

        @Override
        public JedisClientConfigurationBuilder connectTimeout(Duration connectTimeout) {
            Assert.notNull(connectTimeout, "Duration must not be null!");
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Override
        public JedisClientConfiguration build() {
            return new DefaultJedisClientConfiguration(useSsl, sslSocketFactory, sslParameters, hostnameVerifier, usePooling,
                    poolConfig, clientName, readTimeout, connectTimeout);
        }
    }

}
