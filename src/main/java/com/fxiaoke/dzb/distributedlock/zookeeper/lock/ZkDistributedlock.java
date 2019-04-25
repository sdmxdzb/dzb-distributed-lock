package com.fxiaoke.dzb.distributedlock.zookeeper.lock;

import com.fxiaoke.dzb.distributedlock.zookeeper.serializer.MyZkSerializer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;

/***
 *@author lenovo
 *@date 2019/4/24 22:50
 *@Description:
 *@version 1.0
 */
public class ZkDistributedlock implements Lock {
    /*
     * 利用临时顺序节点来实现分布式锁
     * 获取锁：取排队号（创建自己的临时顺序节点），然后判断自己是否是最小号，如是，则获得锁；不是，则注册前一节点的watcher,阻塞等待
     * 释放锁：删除自己创建的临时顺序节点
     */
    private String lockPath;
    private ZkClient client;
    //当前的序号
    private ThreadLocal<String> currentPath = new ThreadLocal<String>();
    //排在我前面的序号
    private ThreadLocal<String> beforePath = new ThreadLocal<String>();
    // 锁重入计数器
    private ThreadLocal<Integer> reenterCount = ThreadLocal.withInitial(() -> 0);

    public ZkDistributedlock(String lockPath) {
        if (StringUtils.isBlank(lockPath)) {
            throw new IllegalArgumentException("patch不能为空字符串");
        }
        this.lockPath = lockPath;
        client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
        //创建父节点
        if (!this.client.exists(lockPath)) {
            try {
                this.client.createPersistent(lockPath, true);
            } catch (ZkNodeExistsException e) {

            }
        }
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        }
    }

    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(Thread.currentThread().getName() + "-----监听到节点变动------->" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(Thread.currentThread().getName() + "-----监听到节点被删除，分布式锁被释放");
                cdl.countDown();
            }
        };
        //监听数据的变化
        client.subscribeDataChanges(this.beforePath.get(), listener);
        //如果存在，就阻塞自己
        if (this.client.exists(this.beforePath.get())) {
            try {
                System.out.println(Thread.currentThread().getName() + "-----分布式锁没抢到，进入阻塞状态");
                cdl.await();
                System.out.println(Thread.currentThread().getName() + "-----释放分布式锁，被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        client.unsubscribeDataChanges(this.beforePath.get(), listener);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        System.out.println(Thread.currentThread().getName() + "-----尝试获取分布式锁");
        if (this.currentPath.get() == null || !client.exists(this.currentPath.get())) {
            String node = this.client.createEphemeralSequential(lockPath + "/", "locked");
            currentPath.set(node);
            //reenterCount.set(0);
        }
        // 获得所有的子
        List<String> children = this.client.getChildren(lockPath);
        // 排序list
        Collections.sort(children);
        // 判断当前节点是否是最小的
        if (currentPath.get().equals(lockPath + "/" + children.get(0))) {
            // 锁重入计数
           // reenterCount.set(reenterCount.get() + 1);
            System.out.println(Thread.currentThread().getName() + "-----获得分布式锁");
            return true;
        } else {
            // 取到前一个
            // 得到字节的索引号
            int curIndex = children.indexOf(currentPath.get().substring(lockPath.length() + 1));
            String node = lockPath + "/" + children.get(curIndex - 1);
            beforePath.set(node);
        }
        return false;
    }

   /* @Override
    public boolean tryLock() {
        //容易产生惊群效应
        try {
            //创建临时节点
            client.createEphemeral(lockPath);
        } catch (ZkNodeExistsException e) {
            return false;
        }
        return true;
    }*/

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "-----释放分布式锁");
        /*if(reenterCount.get() > 1) {
            // 重入次数减1，释放锁
            reenterCount.set(reenterCount.get() - 1);
            return;
        }*/
        // 删除节点
        if (this.currentPath.get() != null) {
            this.client.delete(this.currentPath.get());
            this.currentPath.set(null);
            //this.reenterCount.set(0);
        }
    }
  /*  @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "-----释放分布式锁");
        client.delete(lockPath);
    }*/

    @Override
    public Condition newCondition() {
        return null;
    }
}
