package com.example.myblog.configuration;

import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@MultipartConfig //для приема сервером multipart/form-data через формы
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"com.example.myblog"})
public class WebConfiguration {

    @Bean
    public StandardServletMultipartResolver multipartResolver(){ //для получения файлов
        return new StandardServletMultipartResolver();
    }
}