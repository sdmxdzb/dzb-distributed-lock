package com.fxiaoke.dzb.distributedlock.aop.aspect;

import com.fxiaoke.dzb.distributedlock.exception.SubmitMethodException;
import com.fxiaoke.dzb.distributedlock.redis.JRedisUtils;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * @author: dongzhb
 * @date: 2019/5/10
 * @Description: 防止重复提交操作AOP类
 */
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class ControllerSubmitMethondAspect {

    @Autowired
    private JRedisUtils jedisUtils;

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_TOKEN_FLAG = "tokenFlag";
    private static  final String GET="get";

    /**
     * around
     * @throws Throwable
     */
    @Around(value = "@annotation(com.fxiaoke.dzb.distributedlock.annotation.SubmitAgainAnnotation)")
    public Object excute(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("excute sumbit start!");
        try {
            Object result = null;
            Object[] args = joinPoint.getArgs();
            for(int i = 0;i < args.length;i++){
                if(args[i] != null && args[i] instanceof HttpServletRequest){
                    HttpServletRequest request = (HttpServletRequest) args[i];
                    HttpSession session = request.getSession();
                    if(request.getMethod().equalsIgnoreCase(GET)){
                        //方法为get
                        result = generate(joinPoint, request, session, PARAM_TOKEN_FLAG);
                    }else{
                        //方法为post
                        result = validation(joinPoint, request, session, PARAM_TOKEN_FLAG);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new SubmitMethodException( "prevent duplicate submission method={}",e.getCause());
        }
    }

    public Object generate(ProceedingJoinPoint joinPoint, HttpServletRequest request, HttpSession session,String tokenFlag) throws Throwable {
        String uuid = UUID.randomUUID().toString();
        request.setAttribute(PARAM_TOKEN, uuid);
        return joinPoint.proceed();
    }

    public Object validation(ProceedingJoinPoint joinPoint,
        HttpServletRequest request,
        HttpSession session,String tokenFlag) throws Throwable {
        String requestFlag = request.getParameter(PARAM_TOKEN);
        //redis加锁
        boolean lock = jedisUtils.lock(tokenFlag + requestFlag, requestFlag, 60000);
        log.info("data lock={},{}"  ,lock ,Thread.currentThread().getName());
        System.out.println("进行数据数据枷锁:" + lock + "," + Thread.currentThread().getName());
        System.out.println();
        if(lock){
            //加锁成功
            //执行方法
            Object funcResult = joinPoint.proceed();
            //方法执行完之后进行解锁
            jedisUtils.unLock(tokenFlag + requestFlag, requestFlag);
            return funcResult;
        }else{
            //锁已存在
            throw new SubmitMethodException("prevent duplicate submission");
        }
    }

}
