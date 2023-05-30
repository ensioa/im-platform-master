package com.platform.common.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

/**
 * 程序注解配置
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan({"com.platform.modules.**.dao"})
// 扫描spring工具类
@ComponentScan(basePackages = {"cn.hutool.extra.spring"})
public class ApplicationConfig {

    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return builder -> builder.timeZone(TimeZone.getDefault());
    }

    /**
     * 序列化枚举值为数据库存储值
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Bean
    public static MappingJackson2HttpMessageConverter objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知的枚举字段
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        // 忽略多余的字段不参与序列化
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略null属性字段
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // null属性字段转""
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
                arg1.writeString("");
            }
        });
        SimpleModule simpleModule = new SimpleModule();
        // 格式化Long
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // 格式化时间
        simpleModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT));
            }
        });
        // 格式化金额
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal decimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(decimal.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            }
        });
        // 注册 module
        objectMapper.registerModule(simpleModule);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}