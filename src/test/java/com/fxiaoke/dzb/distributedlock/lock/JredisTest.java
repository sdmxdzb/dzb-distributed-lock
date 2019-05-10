package com.fxiaoke.dzb.distributedlock.lock;

import com.fxiaoke.dzb.distributedlock.redis.JRedisUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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
    }
}
