package org.yky;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yky.controller.interceptor.SMSInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/user/getSMSCode");
    }
}
