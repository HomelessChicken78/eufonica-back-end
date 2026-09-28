package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AccessMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessMethodRepository extends JpaRepository<AccessMethod, UUID> {
}
