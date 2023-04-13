package com.project.cmn.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = "com.project.cmn")
public class Applicaiton extends SpringBootServletInitializer {
    /**
     * @param args Arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Applicaiton.class, args);
    }
}
