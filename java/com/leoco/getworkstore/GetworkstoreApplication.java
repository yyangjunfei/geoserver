package com.leoco.getworkstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.leoco.getworkstore")
public class GetworkstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetworkstoreApplication.class, args);

    }
}