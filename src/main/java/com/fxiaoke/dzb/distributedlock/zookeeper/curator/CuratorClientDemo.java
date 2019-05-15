package com.fxiaoke.dzb.distributedlock.zookeeper.curator;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author: dongzhb
 * @date: 2019/5/14
 * @Description:
 */
public class CuratorClientDemo {
    private static final String URL = "127.0.0.1:2181";
    private static final String path = "/curator";
    /** 两次重试之间等待的初始时间 */
    private static final int baseSleepTimesMs = 1000;
    /** 重试的最大次数 */
    private static final int maxRetries = 3;
    private static final String data = "hello word";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(URL, new ExponentialBackoffRetry(1000, 3));
        client.start();
        if (isExistNode(client, path)) {
            System.out.println("节点不存在");
            System.out.println(crateNode(client, path, CreateMode.PERSISTENT));
        }
        System.out.println(getNodeStat(client, path));
    }

    /**
     * 创建节点
     *
     * @param client 客户端
     * @param path 路径
     * @param createMode 节点类型
     * @return 是否创建成功
     */
    public static boolean crateNode(CuratorFramework client, String path, CreateMode createMode) {
        try {
            client.create().withMode(createMode).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建节点数据
     *
     * @param client 客户端
     * @param path 路径
     * @param createMode 节点类型
     * @param data 节点数据
     * @return 是否创建成功
     */
    public static boolean crateNodeAndData(CuratorFramework client, String path, CreateMode createMode, String data) {
        try {
            client.create().withMode(createMode).forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param client 客户端
     * @param path 路径
     * @return null-节点不存在  | CreateMode.PERSISTENT-是持久化 | CreateMode.EPHEMERAL-临时节点
     */
    public static CreateMode getNodeType(CuratorFramework client, String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            if (stat == null) {
                return null;
            }

            if (stat.getEphemeralOwner() > 0) {
                return CreateMode.EPHEMERAL;
            }

            return CreateMode.PERSISTENT;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param client 客户端
     * @param path 路径
     * @return null-节点不存在  | CreateMode.PERSISTENT-是持久化 | CreateMode.EPHEMERAL-临时节点
     */
    public static Stat getNodeStat(CuratorFramework client, String path) {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(path);
            if (stat == null) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }

    public static boolean isExistNode(CuratorFramework client, String path) {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat == null ? true : false;
    }


    public  void lock(CuratorFramework client, String lockPath){
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        try {
            //等待三秒
            if ( lock.acquire(3L, TimeUnit.SECONDS) )
            {
                try
                {
                    System.out.println("加锁");
                } finally {
                    System.out.println("释放锁");
                    lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
