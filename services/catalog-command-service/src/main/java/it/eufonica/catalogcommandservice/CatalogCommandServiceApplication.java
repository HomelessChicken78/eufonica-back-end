package it.eufonica.catalogcommandservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "it.eufonica.catalogcommandservice",
        "it.eufonica.exception",
        "it.eufonica.security",
        "it.eufonica.event.outbox"
})
@EntityScan(basePackages = {
        "it.eufonica.catalogcommandservice",
        "it.eufonica.event.outbox"
})
@EnableJpaRepositories(basePackages = {
        "it.eufonica.catalogcommandservice",
        "it.eufonica.event.outbox"
})
@EnableScheduling
public class CatalogCommandServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogCommandServiceApplication.class, args);
    }

}
