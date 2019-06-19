package org.springframework.data.redis.connection;

import java.io.Closeable;

public interface RedisSentinelConnection extends RedisSentinelCommands, Closeable {

	/**
	 * @return true if connected to server
	 */
	boolean isOpen();

}
