package com.fxiaoke.dzb.distributedlock.zookeeper.curator;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @author: dongzhb
 * @date: 2019/5/15
 * @Description:
 */
public class CuratorDistributedLock {
    private CuratorFramework client;
    private String lockPath;
    private InterProcessMutex interProcessMutex;
    private static final String URL = "127.0.0.1:2181";
    /** 两次重试之间等待的初始时间 */
    private static final int baseSleepTimesMs = 1000;
    /** 重试的最大次数 */
    private static final int maxRetries = 3;

    public CuratorDistributedLock(String lockPath) {
        this.lockPath = lockPath;
        CuratorFramework client = CuratorFrameworkFactory.newClient(URL, new ExponentialBackoffRetry(baseSleepTimesMs, maxRetries));
        client.start();
        this.lockPath = lockPath;
        interProcessMutex = new InterProcessMutex(client,  lockPath);
    }

    public void acquireLock() {
        try {
            interProcessMutex.acquire(3L, TimeUnit.SECONDS);
            System.out.println("成功获取锁.......");
        } catch (Exception e) {
        }
    }

    /**
     * 释放锁
     */
    public void releaseLock() {
        try {
            if (interProcessMutex != null && interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
                System.out.println("释放锁.......");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Thread:" + Thread.currentThread().getId() + " release distributed lock  exception=" + e);
        }
    }


    public GetDataBuilder getNodeData(){
        GetDataBuilder getDataBuilder =client.getData();

        getDataBuilder.usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("WatchedEvent=="+watchedEvent.getPath()+"="+watchedEvent.getState()+"="+watchedEvent.getType()+"="+watchedEvent.getWrapper());
            }
        });

        getDataBuilder.usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println("CuratorWatcher=="+event.getPath()+"="+event.getState()+"="+event.getType()+"="+event.getWrapper());
            }
        });
        return getDataBuilder;
    }
}
