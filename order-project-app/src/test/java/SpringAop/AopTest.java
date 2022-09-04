package SpringAop;

import com.huawei.shallwe.aop.AnnotationPointcut;
import com.huawei.shallwe.aop.ServiceTimeCostAspect;
import com.huawei.shallwe.aop.ThisPointcutAspect;
import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import com.huawei.shallwe.service.OrderProcessService;
import com.huawei.shallwe.service.impl.OrderProcessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

@Slf4j
public class AopTest {

    @Test
    public void testAspectJProxyFactory() {

        // Target
        OrderProcessServiceImpl orderProcessService = new OrderProcessServiceImpl();
        // AspectJProxyFactory
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory();
        // 设置被代理对象
        aspectJProxyFactory.setTarget(orderProcessService);
        // 设置切面类
        aspectJProxyFactory.addAspect(ServiceTimeCostAspect.class);
        // Proxy
        OrderProcessServiceImpl orderProcessServiceProxy = aspectJProxyFactory.getProxy();
        // 执行方法
        orderProcessServiceProxy.submitOrder(new OrderProcessReq());
        orderProcessServiceProxy.submitOrderWhenThrow();

    }

    @Test
    public void testThisPointCut() {
        OrderProcessServiceImpl orderProcessService = new OrderProcessServiceImpl();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory();
        // 设置需要增强的接口
        Class<?>[] allInterfaces = ClassUtils.getAllInterfaces(orderProcessService);
        aspectJProxyFactory.setInterfaces(allInterfaces);
        // 设置Target
        aspectJProxyFactory.setTarget(orderProcessService);
        aspectJProxyFactory.addAspect(ThisPointcutAspect.class);
        OrderProcessService orderProcessServiceProxy = aspectJProxyFactory.getProxy();
        orderProcessServiceProxy.submitOrder(new OrderProcessReq());
        // 其他判断
        log.info("proxy是否是JDK动态代理对象：{}", AopUtils.isJdkDynamicProxy(orderProcessServiceProxy));
        log.info("proxy是否是cglib代理对象：{}", AopUtils.isCglibProxy(orderProcessServiceProxy));
        log.info("proxy是否是OrderProcessService类型的：{}", OrderProcessService.class.isAssignableFrom(orderProcessServiceProxy.getClass()));
    }


    @Test
    public void testAtWithinPointCut() {
        OrderProcessServiceImpl orderProcessService = new OrderProcessServiceImpl();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory();
        aspectJProxyFactory.setTarget(orderProcessService);
        aspectJProxyFactory.addAspect(AnnotationPointcut.class);
        OrderProcessServiceImpl orderProcessServiceProxy = aspectJProxyFactory.getProxy();
        orderProcessServiceProxy.submitOrder(new OrderProcessReq());
    }

}
