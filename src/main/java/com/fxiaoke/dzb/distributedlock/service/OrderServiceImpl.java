package com.fxiaoke.dzb.distributedlock.service;

import com.fxiaoke.dzb.distributedlock.zookeeper.lock.ZkDistributedlock;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;

/***
 *@author lenovo
 *@date 2019/4/24 22:22
 *@Description:
 *@version 1.0
 */
public class OrderServiceImpl implements OrderService {

    private  static Set<String> orderSet = new HashSet<>();

    Lock lock = new ZkDistributedlock("/order1");

    @Override
    public String getCreateOrderNumber() {
        String orderNumber =null;
        try {
            lock.lock();
             orderNumber =System.currentTimeMillis()+UUID.randomUUID().toString();
            if(orderSet.contains(orderNumber)){
                System.out.println("重复编号:"+orderNumber);
                return null;
            }else {
                orderSet.add(orderNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return orderNumber;
    }
}
