package com.supplysync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontendPath = Paths.get("frontend").toAbsolutePath().toUri().toString();
        if (!frontendPath.endsWith("/")) {
            frontendPath += "/";
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        frontendPath,
                        "file:frontend/"
                );
    }
}
