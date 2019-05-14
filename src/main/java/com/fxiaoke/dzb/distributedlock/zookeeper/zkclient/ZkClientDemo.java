package com.fxiaoke.dzb.distributedlock.zookeeper.zkclient;

import com.fxiaoke.dzb.distributedlock.zookeeper.serializer.MyZkSerializer;
import java.util.List;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/***
 *@author dzb
 *@date 2019/4/19 23:18
 *@Description:  zk测试demo
 *@version 1.0
 */
public class ZkClientDemo {
    private static final String URL = "localhost:2181";
    private static final String path = "/fxiaoke";

    public static void main(String[] args) {
        ZkClient client = new ZkClient(URL);
        //序列化
        client.setZkSerializer(new MyZkSerializer());
        if (!client.exists(path)) {
            //创建节点
            System.out.println("不存在，创建节点");
            client.createPersistent(path, true);
        }

        client.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + "子节点发生变化：" + currentChilds);
            }
        });
        client.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(dataPath + "发生变化：" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath + "节点被删除");
            }
        });

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
