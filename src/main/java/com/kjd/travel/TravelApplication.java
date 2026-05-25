package com.kjd.travel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.kjd.travel.mapper")
@SpringBootApplication
public class TravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelApplication.class, args);
    }

}
