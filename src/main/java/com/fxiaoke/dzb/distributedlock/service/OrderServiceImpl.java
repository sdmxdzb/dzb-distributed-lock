package com.fxiaoke.dzb.distributedlock.service;

import com.fxiaoke.dzb.distributedlock.RandomUtil;
import com.fxiaoke.dzb.distributedlock.zookeeper.lock.ZkDistributedlock;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

/***
 *@author lenovo
 *@date 2019/4/24 22:22
 *@Description:
 *@version 1.0
 */
public class OrderServiceImpl implements OrderService {

    private  static Set<String> orderSet = new HashSet<>();
    Lock lock = new ZkDistributedlock("/order");

    @Override
    public void getCreateOrderNumber() {
        String orderNumber =null;
        try {
            lock.lock();
            //模拟生成订单编号
             orderNumber = RandomUtil.getRandomNumber();
            if(orderSet.contains(orderNumber)){
                System.out.println("已经存在订单编号="+orderNumber);
                return ;
            }else {
                System.out.println("新的订单编号="+orderNumber);
                orderSet.add(orderNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
