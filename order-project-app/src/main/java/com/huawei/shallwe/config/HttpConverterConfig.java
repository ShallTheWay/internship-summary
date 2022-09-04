package com.huawei.shallwe.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class HttpConverterConfig implements WebMvcConfigurer {

    /**
     * Way 1:
     *  create bean of HttpMessageConverter
     * @return
     */
    @Bean
    public HttpMessageConverter fastJsonHttpMessageConverter() {
        // 1. 定义消息转换对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        // 2. 添加Fast Json的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd");
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonConfig.setSerializerFeatures(
                // 美化JSON格式
                SerializerFeature.PrettyFormat,
                // 输出值为null的字段
                SerializerFeature.WriteMapNullValue,
                // List字段如果为null，输出为[]，而非null
                SerializerFeature.WriteNullListAsEmpty,
                // 字符类型字段如果为null，输出为""，而非null
                SerializerFeature.WriteNullStringAsEmpty,
                // 消除对同一对象循环引用的问题（否则有可能会进入死循环）
                SerializerFeature.DisableCircularReferenceDetect

        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }

    /**
     * Way 2:
     *  1. implements WebMvcConfigurer
     *  2. Override configureMessageConverters method
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1. 定义消息转换对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        // 2. 添加Fast Json的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd");
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonConfig.setSerializerFeatures(
                // 美化JSON格式
                SerializerFeature.PrettyFormat,
                // 输出值为null的字段
                SerializerFeature.WriteMapNullValue,
                // List字段如果为null，输出为[]，而非null
                SerializerFeature.WriteNullListAsEmpty,
                // 字符类型字段如果为null，输出为""，而非null
                SerializerFeature.WriteNullStringAsEmpty,
                // 消除对同一对象循环引用的问题（否则有可能会进入死循环）
                SerializerFeature.DisableCircularReferenceDetect

        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //4、将convert添加到converters中
        converters.add(fastJsonHttpMessageConverter);
    }

}
