/**
 * Thinking in java 2018/08/01
 */
package com.fxiaoke.dzb.distributedlock.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import redis.clients.jedis.Jedis;

/**
 * @author dzb
 * @date 2018年10月11日 下午11:03:38
 * @description
 * @version 1.0.0
 */
public class RedisLock implements Lock {
    private static final String LOCK_KEY = "lock_key";
    private static final String SET_IF_NOT_EXIST = "NX";//仅在键不存在时设置键。
    private static final String SET_WITH_EXPIRE_TIME = "PX";//PX表示超时时间是毫秒设置，EX表示超时时间是分钟设置
    private ThreadLocal<String> threadLocal = new ThreadLocal<>();//线程上线文

    //阻塞式
    @Override
    public void lock() {
        if (tryLock()) {
            return;
        } else {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock();
        }
    }

    //非阻塞式锁
    @Override
    public boolean tryLock() {
        String uuid = UUID.randomUUID().toString();
        Jedis rJedis = new Jedis("localhost");
        //原子操作    设置值 + 随机值
        String value = rJedis.set(LOCK_KEY, uuid, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 100);
        if (value != null && value.equals("OK")) {
            threadLocal.set(uuid);
            return true;
        }
        return false;
    }

    //解锁
    @Override
    public void unlock() {
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end ";
        Jedis rJedis = new Jedis("localhost");
        List<String> keys = new ArrayList<>();
        keys.add(LOCK_KEY);
        List<String> args = new ArrayList<>();
        args.add(threadLocal.get());
        Object object = rJedis.eval(script, keys, args);
    }

    //-----------------------

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
