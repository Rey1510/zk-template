package com.rey.template.security.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<AuthenticationFilter>
    authenticationFilterRegistration(
            AuthenticationFilter filter
    ) {
        FilterRegistrationBean<AuthenticationFilter>
                registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);

        return registration;
    }
}
