package it.eufonica.engagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "it.eufonica.engagementservice",
        "it.eufonica.exception",
        "it.eufonica.security",
        "it.eufonica.event.processing",
        "it.eufonica.event.outbox"
})
@EntityScan(basePackages = {
        "it.eufonica.engagementservice",
        "it.eufonica.event.processing",
        "it.eufonica.event.outbox"
})
@EnableJpaRepositories(basePackages = {
        "it.eufonica.engagementservice",
        "it.eufonica.event.processing",
        "it.eufonica.event.outbox"
})
public class EngagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngagementServiceApplication.class, args);
    }

}
