package com.huawei.shallwe.aop;

import com.huawei.shallwe.anno.PcAnno;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Aspect
@Component
public class AnnotationPointcut {

    @Pointcut("@within(com.huawei.shallwe.anno.PcAnno)")
    public void annotationPointcut() {

    }

    @Before("annotationPointcut()")
    public void before(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
        // 取出注解定义的value
        PcAnno declaredAnnotation = (PcAnno) joinPoint.getSignature().getDeclaringType().getDeclaredAnnotation(PcAnno.class);
        System.out.println("-----------" + declaredAnnotation.value() + "-----------");
    }
}
