package com.example.core.mainbody;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@ServletComponentScan
@MapperScan("com.example.**.mapper.**")
public class MainbodyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainbodyApplication.class, args);
    }

}
