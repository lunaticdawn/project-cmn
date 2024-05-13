package com.project.cmn.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = "com.project")
public class Application extends SpringBootServletInitializer {
    /**
     * @param args Arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
