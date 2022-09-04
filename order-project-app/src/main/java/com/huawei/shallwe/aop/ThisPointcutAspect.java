package com.huawei.shallwe.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ThisPointcutAspect {

    /**
     * this(全限定类名)
     *  匹配该类的目标都可以被代理
     */
//  @Pointcut(value = "this(com.huawei.shallwe.service.impl.OrderProcessServiceImpl)")
    @Pointcut(value = "this(com.huawei.shallwe.service.OrderProcessService)")
    public void thisPointcut() {

    }

    @Before(value = "thisPointcut()")
    public void before() {
        System.out.println("---------------this aspect starts---------------");
    }

    @After(value = "thisPointcut()")
    public void after() {
        System.out.println("---------------this aspect ends---------------");
    }
}
