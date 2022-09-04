package SpringAop;

import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import com.huawei.shallwe.service.OrderProcessService;
import com.huawei.shallwe.service.impl.OrderProcessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.CallbackHelper;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class JavaProxyTest {


    /**
     * JDK Dynamic Proxy
     *
     * - Notice: JDK中的Proxy只能为接口生成代理类
     *
     * 1. java.lang.reflect.Proxy#getProxyClass(java.lang.ClassLoader, java.lang.Class[])
     *      为指定的接口创建代理类，返回代理类的Class对象
     * 2. java.lang.reflect.Proxy#newProxyInstance(java.lang.ClassLoader, java.lang.Class[], java.lang.reflect.InvocationHandler)
     *      创建代理类的实例对象
     * 3. java.lang.reflect.Proxy#isProxyClass(java.lang.Class)
     *      判断指定的类是否是一个代理类
     * 4. java.lang.reflect.Proxy#getInvocationHandler(java.lang.Object)
     *      获取代理对象的InvocationHandler对象
     */

    @Test
    public void testJdkDynamicProxy()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 获取代理类
        Class<?> proxyClass = Proxy.getProxyClass(OrderProcessService.class.getClassLoader(), OrderProcessService.class);
        // 通过反射获取代理对象 —— 带有InvocationHandler
        OrderProcessService orderProcessService = (OrderProcessService) proxyClass.getConstructor(InvocationHandler.class).newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("JDK PROXY");
//!             method.invoke(args);
                return null;
            }
        });
        // 使用代理对象
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testJdkDynamicProxyNew() {
        OrderProcessService orderProcessService = (OrderProcessService) Proxy.newProxyInstance(
                OrderProcessService.class.getClassLoader(),     // 类加载器
                new Class<?>[]{OrderProcessService.class},      // 增强接口
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("JDK PROXY");
                        return null;
                    }
                }
        );
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    /**
     * 通过JDK动态代理实现对公共逻辑的封装
     */
    @Test
    public void testJdkDynamicProxyReal() {
        OrderProcessServiceImpl target = new OrderProcessServiceImpl();
//!     OrderProcessService proxy = CostTimeInvocationHandler.createProxy(target, OrderProcessServiceImpl.class);
        OrderProcessService proxy = CostTimeInvocationHandler.createProxy(target, OrderProcessService.class);
        proxy.submitOrder(new OrderProcessReq());
    }

    static class CostTimeInvocationHandler implements InvocationHandler {

        private Object target;

        public CostTimeInvocationHandler(Object obj) {
            target = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long start = System.nanoTime();
            Object result = method.invoke(target, args);
            long end = System.nanoTime();
            log.info(this.target.getClass() + method.getName() + " cost time: {}", end - start);
            return result;
        }

        /**
         * 创建代理对象
         * @param target
         * @param targetInterface
         * @return
         * @param <T>
         */
        public static <T> T createProxy(Object target, Class<T> targetInterface) {
            if (!targetInterface.isInterface()) {   // targetInterface必须是接口类型
                throw new IllegalStateException("JDK Proxy accepts interface!");
            } else if (!targetInterface.isAssignableFrom(target.getClass())) {  // target必须是targetInterface接口的实现类
                throw new IllegalStateException("Target must be the application of interface");
            }
//          return (T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[]{targetInterface}, new CostTimeInvocationHandler(target));
            return (T) Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    new CostTimeInvocationHandler(target)
            );
        }
    }

    /**
     *
     */


    @Test
    public void testCglibEnhance() {
        // 1. 使用Enhancer来创建OrderProcessImpl的代理类
        Enhancer enhancer = new Enhancer();
        // 2. 设置被代理类
        enhancer.setSuperclass(OrderProcessServiceImpl.class);
        // 3. 设置回调函数 —— 实现Callback
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * 代理对象方法拦截器
             * @param o             代理对象
             * @param method        被代理的类方法
             * @param objects       调用方法传递的参数
             * @param methodProxy   方法代理对象
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                log.info("Before " + method.getName());
                return methodProxy.invokeSuper(o, objects); // 调用被代理对象
            }
        });
        // 4. 创建代理对象 —— 注意类型转换
        OrderProcessServiceImpl orderProcessService = (OrderProcessServiceImpl) enhancer.create();
        // CgLib底层通过修改字节码的方式为OrderProcessServiceImpl类创建了一个子类
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testEnhancerFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(OrderProcessServiceImpl.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello Shallwe";
            }
        });
        OrderProcessServiceImpl orderProcessService = (OrderProcessServiceImpl) enhancer.create();
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testEnhancerNoOp() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(OrderProcessServiceImpl.class);
        enhancer.setCallback(NoOp.INSTANCE);
        OrderProcessServiceImpl orderProcessService = (OrderProcessServiceImpl) enhancer.create();
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testEnhancerForDifferentMethods() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(OrderProcessServiceImpl.class);
        enhancer.setCallbacks(
                new Callback[]{
                        new MethodInterceptor() {
                            @Override
                            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                                log.info("Before " + method.getName());
                                return methodProxy.invokeSuper(o, objects); // 调用被代理对象
                            }
                        },
                        new FixedValue() {
                            @Override
                            public Object loadObject() throws Exception {
                                return "Hello Shallwe";
                            }
                        }
                }
        );
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                String methodName = method.getName();
                // 方法名以submit开头，则使用第二个Callback对象来处理，否则使用第一个Callback对象处理
                return methodName.startsWith("submit") ? 1 : 0;
            }
        });
        OrderProcessServiceImpl orderProcessService = (OrderProcessServiceImpl) enhancer.create();
        orderProcessService.submitOrder(new OrderProcessReq());
        orderProcessService.submitOrderWhenThrow();
    }


    @Test
    public void testEnhancerForDifferentMethodsOptimal() {
        Enhancer enhancer = new Enhancer();
        Callback costTimeCallback = new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                long start = System.nanoTime();
                Object result = methodProxy.invokeSuper(o, objects);
                long end = System.nanoTime();
                log.info(o.getClass() + method.getName() + " cost time: {}", end - start);
                return result;
            }
        };
        Callback fixedValueCallback = new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello Shallwe";
            }
        };
        CallbackHelper callbackHelper = new CallbackHelper(OrderProcessService.class, null) {
            @Override
            protected Object getCallback(Method method) {
                return method.getName().startsWith("submit") ? fixedValueCallback : costTimeCallback;
            }
        };
        enhancer.setSuperclass(OrderProcessServiceImpl.class);
        // 调用Enhancer的setCallbacks传递Callback数组
        enhancer.setCallbacks(callbackHelper.getCallbacks());
        // 设置CallbackFilter，用来判断某个方法具体走哪个Callback
        enhancer.setCallbackFilter(callbackHelper);
        OrderProcessServiceImpl orderProcessService = (OrderProcessServiceImpl) enhancer.create();
        orderProcessService.submitOrder(new OrderProcessReq());
    }

    @Test
    public void testCostTimeProxy() {
        OrderProcessServiceImpl orderProcessService = CostTimeProxy.createProxy(new OrderProcessServiceImpl());
        orderProcessService.submitOrder(new OrderProcessReq());

    }

    static class CostTimeProxy implements MethodInterceptor{
        private Object target;

        public CostTimeProxy(Object object) {
            this.target = object;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            long start = System.nanoTime();
            Object result = method.invoke(target, objects);
            long end = System.nanoTime();
            System.out.println(method + "，耗时(纳秒)：" + (end - start));
            return result;
        }

        /**
         * 创建任意类的代理对象
         *
         * @param target
         * @param <T>
         * @return
         */
        public static <T> T createProxy(T target) {
            CostTimeProxy costTimeProxy = new CostTimeProxy(target);
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(costTimeProxy);
            enhancer.setSuperclass(target.getClass());
            return (T) enhancer.create();
        }

    }


}
