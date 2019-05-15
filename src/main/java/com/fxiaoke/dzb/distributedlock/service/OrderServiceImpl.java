package com.fxiaoke.dzb.distributedlock.service;

import com.fxiaoke.dzb.distributedlock.RandomUtil;
import com.fxiaoke.dzb.distributedlock.zookeeper.curator.CuratorDistributedLock;
import com.fxiaoke.dzb.distributedlock.zookeeper.lock.ZkDistributedlock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/***
 *@author lenovo
 *@date 2019/4/24 22:22
 *@Description:
 *@version 1.0
 */
public class OrderServiceImpl implements OrderService {
    private static final String lockPath = "/lock/order";
    private static Set<String> orderSet = new HashSet<>();
    CuratorDistributedLock distributedLock = new CuratorDistributedLock(lockPath);
    @Override
    public void getCreateOrderNumber() {
        Lock lock = new ZkDistributedlock(lockPath);
        String orderNumber = null;
        try {
            lock.lock();
            //模拟生成订单编号
            orderNumber = RandomUtil.getRandomNumber();
            if (orderSet.contains(orderNumber)) {
                System.out.println("已经存在订单编号=" + orderNumber);
                return;
            } else {
                System.out.println("新的订单编号=" + orderNumber);
                orderSet.add(orderNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void createOrder() {
        String orderNumber = null;
        try {
            distributedLock.acquireLock();
            orderNumber = RandomUtil.getRandomNumber();
            if (orderSet.contains(orderNumber)) {
                System.out.println("已经存在订单编号=" + orderNumber);
                return;
            } else {
                System.out.println("新的订单编号=" + orderNumber);
                orderSet.add(orderNumber);
            }
        } catch (Exception e) {
            System.out.println("出现异常:" + e);
        } finally {
            distributedLock.releaseLock();
        }
    }
}
