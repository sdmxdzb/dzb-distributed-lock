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
        int services =5;
        int resuestSize =100;
        CyclicBarrier cyclicBarrier  = new CyclicBarrier(services*resuestSize);
        for (int i = 0; i <services ; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OrderService orderService = new OrderServiceImpl();
                    System.out.println(Thread.currentThread().getName()+"----准备开始下单----");
                    for (int j = 0; j < resuestSize; j++) {
                        new  Thread(new Runnable() {
                            @Override
                            public void run() {
                                //等待service,request 并发数请求I
                                try {
                                    cyclicBarrier.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (BrokenBarrierException e) {
                                    e.printStackTrace();
                                }
                                String orderNumber =orderService.getCreateOrderNumber();
                                System.out.println("订单编号:"+orderNumber);
                            }
                        }).start();
                    }
                }
            }).start();
        }
    }

}
