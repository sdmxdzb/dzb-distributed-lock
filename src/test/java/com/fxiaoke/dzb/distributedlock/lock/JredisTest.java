package com.fxiaoke.dzb.distributedlock.lock;

import com.fxiaoke.dzb.distributedlock.redis.JRedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: dongzhb
 * @date: 2019/5/10
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JredisTest {

    @Autowired
    private JRedisUtils jRedisUtils;

    @Test
    public void test(){
       boolean isLock = jRedisUtils.lock("token","tokenLang",60000);
        System.out.println(isLock);
        if(isLock){
            boolean unLock =  jRedisUtils.unLock("token","tokenLang");
            System.out.println(unLock);
        }
    }
}
