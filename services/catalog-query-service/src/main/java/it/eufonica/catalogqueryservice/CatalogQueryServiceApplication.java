package it.eufonica.catalogqueryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "it.eufonica.catalogqueryservice",
        "it.eufonica.exception",
        "it.eufonica.security",
        "it.eufonica.event.processing"
})
@EntityScan(basePackages = {
        "it.eufonica.catalogqueryservice",
        "it.eufonica.event.processing"
})
@EnableJpaRepositories(basePackages = {
        "it.eufonica.catalogqueryservice",
        "it.eufonica.event.processing"
})
public class CatalogQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogQueryServiceApplication.class, args);
    }

}
