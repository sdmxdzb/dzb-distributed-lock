/**
 * Thinking in java 2018/08/01
 */
package com.fxiaoke.dzb.distributedlock.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author dzb
 * @version 1.0.0
 * @date 2018年8月21日 下午4:58:49
 * @description
 */
public class RedisTemplateLock implements Lock {
    @Value("${redis.lua}")
    private String lua;
    private static final String LOCK_KEY = "lock_key";
    /** 线程上线文 */
    private ThreadLocal<String> threadLocal = new ThreadLocal<>();
    private DefaultRedisScript<String> addScript;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        addScript = new DefaultRedisScript<String>();
        addScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(lua)));
        addScript.setResultType(String.class);
    }

    @Override//阻塞式
    public void lock() {
        if (tryLock()) {
            return;
        } else {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock();
        }
    }

    @Override//非阻塞
    public boolean tryLock() {
        boolean flage = false;
        String uuid = UUID.randomUUID().toString();
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        boolean islock = operations.setIfAbsent(LOCK_KEY, uuid);
        if (islock) {
            operations.set(LOCK_KEY, uuid, 2000, TimeUnit.MICROSECONDS);
            flage = true;
            threadLocal.set(uuid);
        }
        return flage;
    }

    @Override//解锁
    public void unlock() {
        List<String> keys = new ArrayList<>();
        keys.add(LOCK_KEY);
        List<String> args = new ArrayList<>();
        args.add(threadLocal.get());
        Object object = redisTemplate.execute(addScript, redisTemplate.getStringSerializer(), new GenericToStringSerializer<>(String.class), keys, args);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
