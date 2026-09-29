package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRequestRepository extends JpaRepository<ArtistRequest, UUID> {
    boolean existsByRequestingUserAndStatus(AppUser requestingUser, ArtistRequest.RequestStatus status);
}
