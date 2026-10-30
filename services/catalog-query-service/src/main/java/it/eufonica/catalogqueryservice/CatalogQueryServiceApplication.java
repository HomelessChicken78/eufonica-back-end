package it.eufonica.catalogqueryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "it.eufonica.catalogqueryservice",
        "it.eufonica.exception",
        "it.eufonica.security"
})
public class CatalogQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogQueryServiceApplication.class, args);
    }

}
