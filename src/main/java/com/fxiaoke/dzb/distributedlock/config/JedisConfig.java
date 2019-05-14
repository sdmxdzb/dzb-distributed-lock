package com.fxiaoke.dzb.distributedlock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis配置文件
 *
 * @author dzb
 * @version 1.0
 * @date 2018年8月21日 下午3:51:17
 * @description
 */
@Configuration
@EnableCaching
public class JedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout =0;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        /**最小能够保持idel状态的对象数*/
        jedisPoolConfig.setMinIdle(0);
        /**最大能够保持idel状态的对象数 */
        jedisPoolConfig.setMaxIdle(8);
        /**当池内没有返回对象时，最大等待时间*/
        jedisPoolConfig.setMaxWaitMillis(-1);
        /**逐出连接的最小空闲时间 默认1800000毫秒(30分钟)*/
        jedisPoolConfig.setMinEvictableIdleTimeMillis(1800000);
        /**当调用borrow Object方法时，是否进行有效性检查 */
        jedisPoolConfig.setTestOnBorrow(true);
        /**当调用return Object方法时，是否进行有效性检查*/
        jedisPoolConfig.setTestOnReturn(true);
        /**向调用者输出“链接”对象时，是否检测它的空闲超时*/
        jedisPoolConfig.setTestWhileIdle(true);
        /**空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1.*/
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return jedisPool;
    }
}
