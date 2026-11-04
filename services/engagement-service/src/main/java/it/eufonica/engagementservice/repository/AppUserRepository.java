package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findBySub(String sub);
}
