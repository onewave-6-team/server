package org.example.explog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ExpLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpLogApplication.class, args);
    }

}
