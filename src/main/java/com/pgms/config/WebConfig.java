package com.pgms.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry r) {
                r.addMapping("/**").allowedOrigins("http://localhost:3000", "http://localhost:5173").allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS").allowCredentials(false);
            }
        };
    }
}
