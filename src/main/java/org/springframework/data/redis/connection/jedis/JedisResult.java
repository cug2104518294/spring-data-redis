package org.springframework.data.redis.connection.jedis;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.FutureResult;
import org.springframework.lang.Nullable;
import redis.clients.jedis.Response;

import java.util.function.Supplier;

/**
 * Jedis specific FutureResult implementation.
 */
class JedisResult<T, R> extends FutureResult<Response<?>> {

    private final boolean convertPipelineAndTxResults;

    JedisResult(Response<T> resultHolder) {
        this(resultHolder, false, null);
    }

    JedisResult(Response<T> resultHolder, boolean convertPipelineAndTxResults, @Nullable Converter<T, ?> converter) {
        this(resultHolder, () -> null, convertPipelineAndTxResults, converter);
    }

    JedisResult(Response<T> resultHolder, Supplier<R> defaultReturnValue, boolean convertPipelineAndTxResults,
                @Nullable Converter<T, ?> converter) {

        super(resultHolder, converter, defaultReturnValue);
        this.convertPipelineAndTxResults = convertPipelineAndTxResults;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) getResultHolder().get();
    }

    @Override
    public boolean conversionRequired() {
        return convertPipelineAndTxResults;
    }

    /**
     * Jedis specific {@link FutureResult} implementation of a throw away status result.
     */
    static class JedisStatusResult<T, R> extends JedisResult<T, R> {

        @SuppressWarnings("unchecked")
        JedisStatusResult(Response<T> resultHolder, Converter<T, R> converter) {

            super(resultHolder, false, converter);
            setStatus(true);
        }
    }

    /**
     * Builder for constructing JedisResult.
     */
    static class JedisResultBuilder<T, R> {

        private final Response<T> response;
        private Converter<T, R> converter;
        private boolean convertPipelineAndTxResults = false;
        private Supplier<R> nullValueDefault = () -> null;

        @SuppressWarnings("unchecked")
        JedisResultBuilder(Response<T> response) {

            this.response = response;
            this.converter = (source) -> (R) source;
        }

        /**
         * Create a new {@link JedisResultBuilder} given {@link Response}.
         *
         * @param response must not be {@literal null}.
         * @param <T>      native response type.
         * @param <R>      resulting response type.
         * @return the new {@link JedisResultBuilder}.
         */
        static <T, R> JedisResultBuilder<T, R> forResponse(Response<T> response) {
            return new JedisResultBuilder<>(response);
        }

        /**
         * Configure a {@link Converter} to convert between {@code T} and {@code R} types.
         *
         * @param converter must not be {@literal null}.
         * @return {@code this} builder.
         */
        JedisResultBuilder<T, R> mappedWith(Converter<T, R> converter) {

            this.converter = converter;
            return this;
        }

        /**
         * Configure a {@link Supplier} to map {@literal null} responses to a different value.
         *
         * @param supplier must not be {@literal null}.
         * @return {@code this} builder.
         */
        JedisResultBuilder<T, R> mapNullTo(Supplier<R> supplier) {

            this.nullValueDefault = supplier;
            return this;
        }

        JedisResultBuilder<T, R> convertPipelineAndTxResults(boolean flag) {

            convertPipelineAndTxResults = flag;
            return this;
        }

        /**
         * @return a new {@link JedisResult} wrapper with configuration applied from this builder.
         */
        JedisResult<T, R> build() {
            return new JedisResult<>(response, nullValueDefault, convertPipelineAndTxResults, converter);
        }

        /**
         * @return a new {@link JedisStatusResult} wrapper for status results with configuration applied from this builder.
         */
        JedisStatusResult buildStatusResult() {
            return new JedisStatusResult<>(response, converter);
        }
    }
}
