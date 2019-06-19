# RedisTemplate源码解析

## 原始redis操作实现

```java
public void test(){
	// 1.封装config
	JedisPoolConfig poolConfig = new JedisPoolConfig();
	poolConfig.setMaxIdle(20);
	poolConfig.setMaxTotal(200);
	poolConfig.setMaxWaitMillis(2000);
	
	// 2.创建jedis池
	Pool<Jedis> pools = new JedisPool(poolConfig,"localhost",6379,2000,null,false);
	
	// 3.获取jedis
	Jedis jedis = pools.getResource();
	
	// 4.jedis操作
	jedis.set("name".getBytes(), "lucy".getBytes());
	boolean exists = jedis.exists("name");
	System.out.println(exists);
}
```

 主要步骤分四步，主要是创建jedis连接池，然后从连接池中获取jedis连接，最后使用jedis来进行实际操作

RedisTemplate也基本是按照这个步骤来实现的，只是将共有方法抽象出来

## RedisTemplate的实现

**1）创建RedisConfig类，用于创建JedisPoolConfig、RedisConnectionFactory、RedisTemplate**

```java
@Configuration
public class RedisConfig {

@Bean
public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory){
	RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
	redisTemplate.setConnectionFactory(connectionFactory);
	return redisTemplate;
}

@Bean
public RedisConnectionFactory connectionFactory(JedisPoolConfig poolConfig){
	JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
	jedisConnectionFactory.setHostName("localhost");
	jedisConnectionFactory.setPort(6379);
	return jedisConnectionFactory;
}

@Bean
public JedisPoolConfig poolConfig(){
	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	jedisPoolConfig.setMaxIdle(20);
	jedisPoolConfig.setMaxTotal(200);
	jedisPoolConfig.setMaxWaitMillis(2000);
	jedisPoolConfig.setTestOnBorrow(true);
	jedisPoolConfig.setTestOnCreate(true);
	return jedisPoolConfig;
}
}
```
​	真正进行操作的是JedisConnection，该Connection是从RedisConnectionFactory中获取的，故需要先创建该Factory

 **2）创建RedisTest类，进行jedis操作**

```java
public class RedisTest {
public static void main(String[] args) {
	
	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RedisConfig.class);
	RedisTemplate redisTemplate = context.getBean(RedisTemplate.class);
	redisTemplate.opsForValue().set("name", "jack");
	Boolean hasKey = redisTemplate.hasKey("name");
	System.out.println(hasKey);
}
}
```
**3.RedisTemplate源码分析**

 笔者使用的版本为，spring-data-redis-1.8.3.RELEASE.jar

RedisTemplate.hasKey()方法，主要内容如下：


```java
public Boolean hasKey(K key) {
    // 1.将对象类型的key转换为byte[]类型
	final byte[] rawKey = rawKey(key);
    // 2.执行execute方法，主要是为了获取RedisConnection，真正执行在回调方法里
	return execute(new RedisCallback<Boolean>() {
		public Boolean doInRedis(RedisConnection connection) {
            // 3.真正执行方法
			return connection.exists(rawKey);
		}
	}, true);
}  
```
```java
 @Nullable
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
        return execute(action, exposeConnection, false);
    }
```

execute（）方法内容如下：

```java
/**
     * Executes the given action object within a connection that can be exposed or not. Additionally, the connection can
     * be pipelined. Note the results of the pipeline are discarded (making it suitable for write-only scenarios).
     */
    @Nullable
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {

        Assert.isTrue(initialized, "template not initialized; call afterPropertiesSet() before using it");
        Assert.notNull(action, "Callback object must not be null");

        //1.获取连接池工厂，即RedisConfig类中的RedisConnectionFactory bean
        RedisConnectionFactory factory = getRequiredConnectionFactory();
        RedisConnection conn = null;
        try {
            // 2.获取连接
            if (enableTransactionSupport) {
                // only bind resources in case of potential transaction synchronization
                conn = RedisConnectionUtils.bindConnection(factory, enableTransactionSupport);
            } else {
                conn = RedisConnectionUtils.getConnection(factory);
            }

            boolean existingConnection = TransactionSynchronizationManager.hasResource(factory);
            RedisConnection connToUse = preProcessConnection(conn, existingConnection);

            boolean pipelineStatus = connToUse.isPipelined();
            if (pipeline && !pipelineStatus) {
                connToUse.openPipeline();
            }

            RedisConnection connToExpose = (exposeConnection ? connToUse : createRedisConnectionProxy(connToUse));
            // 3.封装连接后，将连接传入回调函数，然后执行真正的操作
            T result = action.doInRedis(connToExpose);

            // close pipeline
            if (pipeline && !pipelineStatus) {
                connToUse.closePipeline();
            }

            // TODO: any other connection processing?
            return postProcessResult(result, connToUse, existingConnection);
        } finally {
            RedisConnectionUtils.releaseConnection(conn, factory);
        }
    }
```
下面对execute（）方法逐步解析

1）获取连接池工厂

```java
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
```

```java
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
```





 获取工厂这一步，直接从RedisAccessor类中获取，RedisTemplate类继承了RedisAccessor，而在RedisConfig类中可以看到，该RedisConnectionFactory既是我们手动创建的bean


```java
2）获取连接

该步骤可定位到RedisConnectionUtils中，代码如下

public static RedisConnection doGetConnection(RedisConnectionFactory factory, boolean allowCreate, boolean bind,
		boolean enableTransactionSupport) {
    ...
    // 主要就是这步操作
	RedisConnection conn = factory.getConnection();
    ...
	return conn;
}
具体实现在JedisConnectionFactory.getConnection（）中，代码如下

public RedisConnection getConnection() {
 
	if (cluster != null) {
		return getClusterConnection();
	}
 
	Jedis jedis = fetchJedisConnector();
    
    // 主要步骤
	JedisConnection connection = (usePool ? new JedisConnection(jedis, pool, dbIndex, clientName)
			: new JedisConnection(jedis, null, dbIndex, clientName));
	connection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
	return postProcessConnection(connection);
}
```


​    看JedisConnection的源码可知，其封装了Jedis、Pool等域

```java
 3）执行回调函数

public Boolean hasKey(K key) {
    // 1.将对象类型的key转换为byte[]类型
	final byte[] rawKey = rawKey(key);
 
    // 2.执行execute方法，主要是为了获取RedisConnection，真正执行在回调方法里
	return execute(new RedisCallback<Boolean>() {
 
		public Boolean doInRedis(RedisConnection connection) {
            // 3.真正执行方法
			return connection.exists(rawKey);
		}
	}, true);
}
可以看到真正执行的为connection.exists方法，具体实现类为JedisConnection，方法内容如下：

public Boolean exists(byte[] key) {
	try {
		if (isPipelined()) {
			pipeline(new JedisResult(pipeline.exists(key)));
			return null;
		}
		if (isQueueing()) {
			transaction(new JedisResult(transaction.exists(key)));
			return null;
		}
        // 真正执行方法
		return jedis.exists(key);
	} catch (Exception ex) {
		throw convertJedisAccessException(ex);
	}
}
```


总结以上步骤可看到，框架封装了获取Connection的过程，用户只需要获取Connection后，直接调用Connection相关方法来操作即可

类似于模板模式，将公共过程抽象出去，用户只关心实际行为即可