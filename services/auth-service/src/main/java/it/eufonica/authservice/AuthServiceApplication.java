package it.eufonica.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "it.eufonica.authservice",
        "it.eufonica.exception",
        "it.eufonica.security",
        "it.eufonica.event.outbox"
})
@EntityScan(basePackages = {
        "it.eufonica.authservice",
        "it.eufonica.event.outbox"
})
@EnableJpaRepositories(basePackages = {
        "it.eufonica.authservice",
        "it.eufonica.event.outbox"
})
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
