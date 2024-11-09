package com.example.clearnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ClearnoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearnoteApplication.class, args);
    }

}
