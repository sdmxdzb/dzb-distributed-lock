package com.fxiaoke.dzb.distributedlock;

import java.util.Random;

/**
 * @author: dongzhb
 * @date: 2019/4/25
 * @Description:
 */
public class RandomUtil {

    public static  String getRandomNumber(){
        Random random = new Random();
        int x = random.nextInt(99999999);
            x=x+100000000;
        return String.valueOf(x);
    }

    public static void main(String[] args) {
        System.out.println(getRandomNumber());
    }
}
