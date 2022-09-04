package SpringAop;

import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import com.huawei.shallwe.service.impl.OrderProcessServiceImpl;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;

public class SpringAopTest {

    @Test
    public void testSpringAop1() {
        // Target
        OrderProcessServiceImpl target = new OrderProcessServiceImpl();
        // Pointcut
        Pointcut pointcut = new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> OrderProcessServiceImpl.class.isAssignableFrom(clazz);
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher(){

                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return method.getName().startsWith("submit");
                    }

                    @Override
                    public boolean isRuntime() {
                        return false;
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object... args) {
                        return false;
                    }
                };
            }
        };
        // Advice
        MethodBeforeAdvice advice = (method, args, target_) -> System.out.println(method.getName() + ": Hello");
        // Advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
        // ProxyFactory
        ProxyFactory proxyFactory = new ProxyFactory();
        // Settings of ProxyFactory
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        // Proxy
        OrderProcessServiceImpl proxy = (OrderProcessServiceImpl) proxyFactory.getProxy();
        // Execution
        proxy.submitOrder(new OrderProcessReq());
    }

    /**
     * 统计一下work方法的耗时，将耗时输出
     */
    @Test
    public void testSpringAop2() {
        OrderProcessServiceImpl target = new OrderProcessServiceImpl();
        Pointcut pointcut = new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> OrderProcessServiceImpl.class.isAssignableFrom(clazz);
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return method.getName().startsWith("submit");
                    }

                    @Override
                    public boolean isRuntime() {
                        return false;
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object... args) {
                        return false;
                    }
                };
            }
        };
        MethodInterceptor methodInterceptor = new MethodInterceptor(){
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("准备调用: " + invocation.getMethod());
                long starTime = System.nanoTime();
                Object result = invocation.proceed();
                long endTime = System.nanoTime();
                System.out.println(invocation.getMethod() + "，调用结束！");
                System.out.println("耗时(纳秒): " + (endTime - starTime));
                return result;
            }
        };
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        OrderProcessServiceImpl proxy = (OrderProcessServiceImpl) proxyFactory.getProxy();
        proxy.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testSpringAop3() {
        OrderProcessServiceImpl target = new OrderProcessServiceImpl();
        Pointcut pointcut = new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> OrderProcessServiceImpl.class.isAssignableFrom(clazz);
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return method.getName().startsWith("submit");
                    }

                    @Override
                    public boolean isRuntime() {
                        return true;
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object... args) {
                        if (args.length == 1 && args[0].getClass().equals(OrderProcessReq.class)) {
                            return true;
                        }
                        return false;
                    }
                };
            }
        };
        AfterReturningAdvice advice = (returnValue, method, args, target_) -> System.out.println("Hello There!");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        OrderProcessServiceImpl proxy = (OrderProcessServiceImpl) proxyFactory.getProxy();
        proxy.submitOrder(new OrderProcessReq());
    }

}
