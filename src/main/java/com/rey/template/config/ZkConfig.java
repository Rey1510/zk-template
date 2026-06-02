package com.rey.template.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

@Configuration
public class ZkConfig {

    @Bean
    public ServletRegistrationBean<DHtmlLayoutServlet> zkLoader() {

        System.out.println("REGISTERING ZK LOADER");

        ServletRegistrationBean<DHtmlLayoutServlet> bean =
                new ServletRegistrationBean<>(
                        new DHtmlLayoutServlet(),
                        "*.zul"
                );

        bean.addInitParameter("update-uri", "/zkau");
        bean.setLoadOnStartup(1);

        return bean;
    }

    @Bean
    public ServletRegistrationBean<DHtmlUpdateServlet> zkAu() {

        ServletRegistrationBean<DHtmlUpdateServlet> bean =
                new ServletRegistrationBean<>(
                        new DHtmlUpdateServlet(),
                        "/zkau/*"
                );

        bean.setLoadOnStartup(2);

        return bean;
    }
}