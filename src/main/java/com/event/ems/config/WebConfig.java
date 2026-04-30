package com.event.ems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 🔥 EXACT FIX
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        String location = "file:" + uploadDir.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}