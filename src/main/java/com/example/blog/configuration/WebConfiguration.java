package com.example.blog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class WebConfiguration {

    @Bean()
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}