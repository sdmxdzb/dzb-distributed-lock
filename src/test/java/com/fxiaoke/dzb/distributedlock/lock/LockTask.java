/**
 * Thinking in java 2018/08/01
 */
package com.fxiaoke.dzb.distributedlock.lock;

import com.fxiaoke.dzb.distributedlock.redis.RedisLock;
import com.fxiaoke.dzb.distributedlock.util.PrinterMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * @author dzb
 * @date 2018年8月21日 下午3:51:17
 * @description
 * 什么事线程安全问题:
 * 当多个线程访问某个类时，不管运行时环境采用何种调度方式或者这些线程将如何交替执行，
 *            并且在主调代码中不需要任何额外的同步或协同，这个类都能表现出正确的行为，那么这个类就是线程安全的。
 * @version 1.0.0
 *
 * 单机的锁
 */
public class LockTask implements Runnable {

    private String content;
    public LockTask(String content) {
        this.content = content;
    }
    private static int threadnum = 1;
    //倒计数(发令枪) 闭锁  用于制造线程的并发执行
    private static CountDownLatch cld = new CountDownLatch(threadnum);
    private static Lock lock = new RedisLock();//ReentrantLock();

    @Override
    public void run() {
        try {
            cld.await(); //等待其它线程结束  等待发令枪计数器变为0
            lock.lock(); //阻塞式锁
            //lock.tryLock(); // 非阻塞式锁
            PrinterMessage.printerMessage(Thread.currentThread().getName() + "(" + content + ")");
            //
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void Test() {
        for (int i = 0; i < 10; i++) {
            System.out.println("开始测试......");
            new Thread(new LockTask("测试数据")).start();
            cld.countDown();
        }
    }
	
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println("开始测试......");
			new Thread(new LockTask("测试数据")).start();
			cld.countDown();
		}
	}
}
