package org.springframework.data.redis.connection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * The result of an asynchronous operation
 */
public abstract class FutureResult<T> {

    private T resultHolder;

    private final Supplier<?> defaultConversionResult;

    private boolean status = false;

    @SuppressWarnings("rawtypes")
    protected Converter converter;

    /**
     * Create new {@link FutureResult} for given object actually holding the result itself.
     */
    public FutureResult(T resultHolder) {
        this(resultHolder, val -> val);
    }

    /**
     * Create new {@link FutureResult} for given object actually holding the result itself and a converter capable of
     * transforming the result via {@link #convert(Object)}.
     */
    @SuppressWarnings("rawtypes")
    public FutureResult(T resultHolder, @Nullable Converter converter) {
        this(resultHolder, converter, () -> null);
    }

    /**
     * Create new {@link FutureResult} for given object actually holding the result itself and a converter capable of
     * transforming the result via {@link #convert(Object)}.
     */
    public FutureResult(T resultHolder, @Nullable Converter converter, Supplier<?> defaultConversionResult) {
        this.resultHolder = resultHolder;
        this.converter = converter != null ? converter : val -> val;
        this.defaultConversionResult = defaultConversionResult;
    }

    /**
     * Get the object holding the actual result.
     */
    public T getResultHolder() {
        return resultHolder;
    }

    /**
     * Converts the given result if a converter is specified, else returns the result
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public Object convert(@Nullable Object result) {

        if (result == null) {
            return computeDefaultResult(null);
        }

        return computeDefaultResult(converter.convert(result));
    }

    @Nullable
    private Object computeDefaultResult(@Nullable Object source) {
        return source != null ? source : defaultConversionResult.get();
    }

    @SuppressWarnings("rawtypes")
    public Converter getConverter() {
        return converter;
    }

    /**
     * Indicates if this result is the status of an operation. Typically status results will be discarded on conversion.
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Indicates if this result is the status of an operation. Typically status results will be discarded on conversion.
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return The result of the operation. Can be {@literal null}.
     */
    @Nullable
    public abstract Object get();

    /**
     * Indicate whether or not the actual result needs to be {@link #convert(Object) converted} before handing over.
     */
    public abstract boolean conversionRequired();
}
