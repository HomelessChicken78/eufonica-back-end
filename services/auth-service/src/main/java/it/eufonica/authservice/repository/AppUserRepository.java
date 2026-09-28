package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
}
