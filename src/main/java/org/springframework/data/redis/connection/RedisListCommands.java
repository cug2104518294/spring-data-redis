package org.springframework.data.redis.connection;

import org.springframework.lang.Nullable;

import java.util.List;

public interface RedisListCommands {

    enum Position {
        BEFORE, AFTER
    }

    @Nullable
    Long rPush(byte[] key, byte[]... values);

    @Nullable
    Long lPush(byte[] key, byte[]... values);

    @Nullable
    Long rPushX(byte[] key, byte[] value);

    @Nullable
    Long lPushX(byte[] key, byte[] value);

    @Nullable
    Long lLen(byte[] key);

    @Nullable
    List<byte[]> lRange(byte[] key, long start, long end);

    void lTrim(byte[] key, long start, long end);

    @Nullable
    byte[] lIndex(byte[] key, long index);

    @Nullable
    Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value);

    void lSet(byte[] key, long index, byte[] value);

    @Nullable
    Long lRem(byte[] key, long count, byte[] value);

    @Nullable
    byte[] lPop(byte[] key);

    @Nullable
    byte[] rPop(byte[] key);

    @Nullable
    List<byte[]> bLPop(int timeout, byte[]... keys);

    @Nullable
    List<byte[]> bRPop(int timeout, byte[]... keys);

    @Nullable
    byte[] rPopLPush(byte[] srcKey, byte[] dstKey);

    @Nullable
    byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey);
}
