package com.fxiaoke.dzb.distributedlock.service;

public interface OrderService {
    /**
     * 生成订单
     * */
    public void getCreateOrderNumber();

    /**
     * 下订单
     * */
    public void createOrder();

}
