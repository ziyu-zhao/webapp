package com.example.csye6225_zzy.log;

import com.amazonaws.util.EC2MetadataUtils;
import com.timgroup.statsd.StatsDClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DiyLog {

    @Autowired
    private StatsDClient statsDClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.example.csye6225_zzy.controller.*.*(..))")
    public Object aroundWebApi(ProceedingJoinPoint joinPoint) throws Throwable {
        String sign = String.valueOf(joinPoint.getSignature());
        logger.info(sign + " called");
        statsDClient.incrementCounter(sign);
        long executeTime1 = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executeTime2 = System.currentTimeMillis();
        statsDClient.recordExecutionTime(sign,(executeTime2-executeTime1));
        return result + " \nlocal address: "+EC2MetadataUtils.getPrivateIpAddress();
    }

    @Around("execution(* com.example.csye6225_zzy.service.*.*(..))")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        String sign = String.valueOf(joinPoint.getSignature());
        long executeTime1 = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executeTime2 = System.currentTimeMillis();
        statsDClient.recordExecutionTime(sign,(executeTime2-executeTime1));
        return result;
    }
    
}
