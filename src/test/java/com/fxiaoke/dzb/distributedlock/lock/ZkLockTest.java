package com.fxiaoke.dzb.distributedlock.lock;

import com.fxiaoke.dzb.distributedlock.service.OrderService;
import com.fxiaoke.dzb.distributedlock.service.OrderServiceImpl;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import lombok.extern.slf4j.Slf4j;

/***
 *@author lenovo
 *@date 2019/4/24 23:09
 *@Description:
 *@version 1.0
 */
@Slf4j
public class ZkLockTest {

    public static void main(String[] args) {
        //模拟当前服务器数
        int services =5;
        //每台服务器并发数
        int requestSize =100;
        //栅栏处理并发
        CyclicBarrier cyclicBarrier  = new CyclicBarrier(services*requestSize);
        for (int i = 0; i <services ; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OrderService orderService = new OrderServiceImpl();
                    System.out.println(Thread.currentThread().getName()+"----准备开始下单----");
                    for (int j = 0; j < requestSize; j++) {
                        new  Thread(new Runnable() {
                            @Override
                            public void run() {
                                //等待service,request 并发数请求
                                try {
                                    cyclicBarrier.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (BrokenBarrierException e) {
                                    e.printStackTrace();
                                }
                               orderService.getCreateOrderNumber();
                            }
                        }).start();
                    }
                }
            }).start();
        }
    }

}
