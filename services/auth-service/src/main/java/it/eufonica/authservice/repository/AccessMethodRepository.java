package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccessMethodRepository extends JpaRepository<AccessMethod, UUID> {
    boolean existsByProviderNameAndProviderUserId(String cognito, String providerUserId);

    Optional<AccessMethod> findByProviderNameAndProviderUserId(String providerName, String providerUserId);

    Optional<AccessMethod> findByUser(AppUser user);
}
