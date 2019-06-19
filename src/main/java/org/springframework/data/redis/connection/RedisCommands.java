package org.springframework.data.redis.connection;

import org.springframework.lang.Nullable;

/**
 * Interface for the commands supported by Redis.
 */
public interface RedisCommands extends RedisKeyCommands, RedisStringCommands, RedisListCommands, RedisSetCommands,
		RedisZSetCommands, RedisHashCommands, RedisTxCommands, RedisPubSubCommands, RedisConnectionCommands,
		RedisServerCommands, RedisStreamCommands, RedisScriptingCommands, RedisGeoCommands, RedisHyperLogLogCommands {

	/**
	 * 'Native' or 'raw' execution of the given command along-side the given arguments. The command is executed as is,
	 * with as little 'interpretation' as possible - it is up to the caller to take care of any processing of arguments or
	 * the result.
	 *
	 * @param command Command to execute. must not be {@literal null}.
	 * @param args Possible command arguments (may be empty).
	 * @return execution result. Can be {@literal null}.
	 */
	@Nullable
	Object execute(String command, byte[]... args);
}
