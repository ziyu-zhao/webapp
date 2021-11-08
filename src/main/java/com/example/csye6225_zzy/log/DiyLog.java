package com.example.csye6225_zzy.log;

import com.timgroup.statsd.StatsDClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DiyLog {

    @Autowired
    private StatsDClient statsDClient;

    @Around("execution(* com.example.csye6225_zzy.controller.*.*(..))")
    public void aroundWebApi(ProceedingJoinPoint joinPoint) throws Throwable {
        String sign = String.valueOf(joinPoint.getSignature());
        statsDClient.incrementCounter(sign);
        long executeTime1 = System.currentTimeMillis();
        joinPoint.proceed();
        long executeTime2 = System.currentTimeMillis();
        statsDClient.recordExecutionTime(sign,(executeTime2-executeTime1));
    }

    @Around("execution(* com.example.csye6225_zzy.service.*.*(..))")
    public void aroundS3(ProceedingJoinPoint joinPoint) throws Throwable {
        String sign = String.valueOf(joinPoint.getSignature());
        long executeTime1 = System.currentTimeMillis();
        joinPoint.proceed();
        long executeTime2 = System.currentTimeMillis();
        statsDClient.recordExecutionTime(sign,(executeTime2-executeTime1));
    }
    
}
