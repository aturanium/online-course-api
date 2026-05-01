package com.onlinecourse.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.onlinecourse.service",
    "com.onlinecourse.repository",
    "com.onlinecourse.security"
})
public class AppConfig {

}
