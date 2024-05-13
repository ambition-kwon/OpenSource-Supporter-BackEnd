package me.jejunu.opensource_supporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class OpensourceSupporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpensourceSupporterApplication.class, args);
    }

}
