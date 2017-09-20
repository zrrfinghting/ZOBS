package com.zero;

import com.zero.logic.util.ReadProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 自定义配置类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/20
 */
@Configuration
public class StateResourceConfigurer extends WebMvcConfigurerAdapter{
    /**
     * 配置访问静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){

        //读取静态资源存放路径
        String resourcePath = ReadProperties.getProperties().getProperty("resourcePath");
        String folder=ReadProperties.getProperties().getProperty("folder");
        //registry.addResourceHandler("/bookpicture/**").addResourceLocations("file:C:/bookpicture/");  file:C:/bookpicture/
        registry.addResourceHandler("/"+folder+"/**").addResourceLocations(resourcePath);
        super.addResourceHandlers(registry);
    }
}
