package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.ArtistAuthProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistAuthProjection, UUID> {
    Optional<ArtistAuthProjection> findFirstByName(String name);
}
