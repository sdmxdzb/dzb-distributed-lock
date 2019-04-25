package com.fxiaoke.dzb.distributedlock.zookeeper.serializer;

import java.io.UnsupportedEncodingException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/***
 *@author lenovo
 *@date 2019/4/19 23:21
 *@Description:
 *@version 1.0
 */
public class MyZkSerializer implements ZkSerializer {
    private static final String charsetName="UTF-8";
    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        String str =(String)data;
        try {
            return str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes,charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
