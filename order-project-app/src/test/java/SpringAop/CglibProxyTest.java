package SpringAop;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CglibProxyTest {

    /**
     * 创建多个接口的动态代理
     */
    @Test
    public void testMultiInterface() {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{IService1.class, IService2.class});
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                log.info("Object o: {}", JSON.toJSONString(o));
                log.info("Method method: {}", method);
                log.info("Object[] objects: ");
                for (Object object : objects) {
                    log.info(JSON.toJSONString(object));
                }
                log.info("MethodProxy methodProxy: {}", methodProxy);
                return null;
            }
        });
        Object proxy = enhancer.create();
        if (proxy instanceof IService1) {
            System.out.println("proxy instanceof IService1");
        }
        if (proxy instanceof IService2) {
            System.out.println("proxy instanceof IService2");
        }
        log.info("Class of proxy:{}", proxy.getClass());
        System.out.println("创建代理类实现的接口如下：");
        for (Class<?> cs : proxy.getClass().getInterfaces()) {
            System.out.println(cs);
        }
    }


    @Test
    public void testClassAndInterface() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                long startime = System.nanoTime();
                Object result = methodProxy.invokeSuper(o, objects); //调用父类中的方法
                System.out.println(method + "，耗时(纳秒):" + (System.nanoTime() - startime));
                return result;
            }
        });
        Object proxy = enhancer.create();
        //判断代理对象是否是Service类型的
        System.out.println("proxy instanceof Service: " + (proxy instanceof Service));
        if (proxy instanceof Service) {
            Service service = (Service) proxy;
            service.m1();
            service.m2();
        }
        // 看一下代理对象的类型
        System.out.println(proxy.getClass());
        // 输出代理对象的父类
        System.out.println("代理类的父类：" + proxy.getClass().getSuperclass());
        // 看一下代理类实现的接口
        System.out.println("创建代理类实现的接口如下：");
        for (Class<?> cs : proxy.getClass().getInterfaces()) {
            System.out.println(cs);
        }
    }

    static interface IService1{
        void m1();
    }

    static interface IService2{
        void m2();
    }

    static class Service implements IService1, IService2 {
        @Override
        public void m1() {
            System.out.println("Service.m1()");
        }

        @Override
        public void m2() {
            System.out.println("Service.m2()");
        }
    }

    /**
     * LazyLoader的使用
     */
    @Test
    public void testLazyLoader() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserModel.class);
        enhancer.setCallback(new LazyLoader() {
            @Override
            public Object loadObject() throws Exception {
                System.out.println("第一次创建UserModel对象");
                // 首次创建后将被代理对象放入缓存中
                return new UserModel("Shallwe");
            }
        });
        UserModel userModel = (UserModel) enhancer.create();
        log.info("1st time, userModel.say(): ");
        userModel.say();
        log.info("2nd time, userModel.say(): ");
        userModel.say();
    }

    static class UserModel{
        private String name;

        public UserModel() {
        }

        public UserModel(String name) {
            this.name = name;
        }

        public void say() {
            System.out.println("你好：" + name);
        }
    }


    /**
     *
     */
    @Test
    public void testLazyLoadingBlog() {
        //创建博客对象
        BlogModel blogModel = new BlogModel();
        System.out.println(blogModel.title);
//!     log.info("blogModel: {}", JSON.toJSONString(blogModel));
        System.out.println("博客内容");
        System.out.println(blogModel.blogContentModel.getContent());
        System.out.println(blogModel.blogContentModel.getContent());
//!     log.info("blogModel: {}", JSON.toJSONString(blogModel));
    }

    static class BlogModel{
        private String title;

        private BlogContentModel blogContentModel;

        public BlogModel() {
            this.title = "Blog of Shallwe";
            this.blogContentModel = this.getBlogContentModel();
        }

        public BlogContentModel getBlogContentModel() {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(BlogContentModel.class);
            enhancer.setCallback(new LazyLoader() {
                @Override
                public Object loadObject() throws Exception {
                    log.info("Fetching the blog content from database...");

                    log.info("Successful to fetch blogContentModel.");
                    return new BlogContentModel("Spring AOP - CGLIB");
                }
            });
            return (BlogContentModel) enhancer.create();
        }

        @Setter
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        static class BlogContentModel {
            String content;
        }
    }

    /**
     *
     */
    @Test
    public void testDispatcher() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserModel.class);
        //创建一个Dispatcher对象
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public Object loadObject() throws Exception {
                System.out.println("调用Dispatcher.loadObject()方法");
                return new UserModel("路人甲java," + UUID.randomUUID().toString());
            }
        };
        enhancer.setCallback(dispatcher);
        Object proxy = enhancer.create();
        UserModel userModel = (UserModel) proxy;
        System.out.println("第1次调用say方法");
        userModel.say();
        System.out.println("第2次调用say方法");
        userModel.say();
    }

    /**
     *
     */
    @Test
    public void testUserServiceByDispatcher() {
        Class<?> targetClass = UserService.class;
        IMethodInfo methodInfo = new DefaultMethodInfo(targetClass);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setInterfaces(new Class[]{IMethodInfo.class});
        enhancer.setCallbacks(new Callback[]{
                new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        return methodProxy.invokeSuper(o, objects);
                    }
                },
                new Dispatcher() {
                    @Override
                    public Object loadObject() throws Exception {
                        // 由该对象来处理代理对象中所有MethodInfo接口中的所有方法
                        return methodInfo;
                    }
                }
        });
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                return method.getDeclaringClass().equals(IMethodInfo.class) ? 1 : 0;
            }
        });
        UserService proxy = (UserService) enhancer.create();
        if (proxy instanceof UserService) {
            log.info("proxy instance of UserService");
            ((UserService) proxy).add();
        }
        if (proxy instanceof IMethodInfo) {
            log.info("proxy instance of IMethodInfo");
            System.out.println(((IMethodInfo) proxy).methodCount());;
            System.out.println(((IMethodInfo) proxy).methodNames());
        }

    }

    static class UserService {
        public void add() {
            System.out.println("新增用户");
        }

        public void update() {
            System.out.println("更新用户信息");
        }
    }

    //用来获取方法信息的接口
    interface IMethodInfo {
        //获取方法数量
        int methodCount();

        //获取被代理的对象中方法名称列表
        List<String> methodNames();
    }

    class DefaultMethodInfo implements IMethodInfo {

        private Class<?> targetClass;

        public DefaultMethodInfo(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public int methodCount() {
            return targetClass.getMethods().length;
        }

        @Override
        public List<String> methodNames() {
            return Arrays.stream(targetClass.getMethods())
                    .map(Method::getName)
                    .collect(Collectors.toList());
        }
    }

}
