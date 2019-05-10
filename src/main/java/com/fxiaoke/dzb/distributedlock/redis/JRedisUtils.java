package com.fxiaoke.dzb.distributedlock.redis;

import com.fxiaoke.dzb.distributedlock.config.JedisConfig;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JRedisUtils {
	
	/***
	互斥性。在任意时刻，只有一个客户端能持有锁。
	不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
	具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
	解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
	 * **/
	

	@Autowired
	private JedisConfig jedisConfig;
	
	private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";//仅在键不存在时设置键。
    private static final String SET_WITH_EXPIRE_TIME = "PX";//PX表示超时时间是毫秒设置，EX表示超时时间是分钟设置
    private static final Long RELEASE_SUCCESS = 1L;
    
	private ThreadLocal<String> threadLocal  = new ThreadLocal<>();//线程上线文
	
	private JedisPool getJedisPool(){
		return jedisConfig.redisPoolFactory();
	}
	
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间  （毫秒）
     * @return 是否获取成功
     */
    public boolean lock(String lockKey, String requestId, int expireTime) {
    	JedisPool jedisPool = getJedisPool();
    	//从连接池获取连接  
    	Jedis jedis = null;  
    	try{  
    	   jedis = jedisPool.getResource();  
    	   String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
           if (LOCK_SUCCESS.equals(result)) {
        	   threadLocal.set(requestId);
        	   return true;
           }
           return false;
    	}catch(Exception e) {  
    	   e.printStackTrace(); 
    	   return false;
    	}finally{  
    	   //归还连接到redis池中  
    		jedis.close();
    	} 
    }
    
    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean unLock(String lockKey, String requestId) {
    	JedisPool jedisPool = getJedisPool();
    	//从连接池获取连接  
    	Jedis jedis = null;  
    	try{  
    	   jedis = jedisPool.getResource();  
    	   String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            requestId =threadLocal.get();
    	   Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
           if (RELEASE_SUCCESS.equals(result)) {
               return true;
           }
           return false;
    	}catch(Exception e) {  
    	   e.printStackTrace(); 
    	   return false;
    	}finally{  
    	   //归还连接到redis池中  
    		jedis.close();
    	} 
    }
	
}