package com.huawei.shallwe.aop;

import com.sun.org.apache.xpath.internal.operations.String;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceTimeCostAspect {

    @Pointcut("execution(public * com.huawei.shallwe.service.impl.*ServiceImpl.*(..))")
    public void serviceTimeCostPointcut() {

    }

    @Before(value = "serviceTimeCostPointcut()")
    public void beforeServiceTimeCost() {
        log.info("ServiceTimeCost --> beforeServiceTimeCost");
    }

    @AfterThrowing(value = "serviceTimeCostPointcut()", throwing = "e")
    public void afterServiceTimeCostThrow(JoinPoint joinPoint, Exception e) {
        log.error(joinPoint.getSignature().getName() + " throws Exception: {}", e.getMessage());
    }

    @Around(value = "serviceTimeCostPointcut()")
    public void aroundServiceTimeCost(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        joinPoint.proceed();
        long end = System.currentTimeMillis();
        StringBuilder output = new StringBuilder();
        output.append(joinPoint.getSignature().getDeclaringTypeName())
                .append(".").append(joinPoint.getSignature().getName()).append("() --> time cost: {}");
        log.info(output.toString(), end - start);
    }
}
