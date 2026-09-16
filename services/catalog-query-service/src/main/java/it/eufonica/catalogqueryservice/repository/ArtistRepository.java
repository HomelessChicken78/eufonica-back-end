package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.ArtistRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistRead, UUID> {
}
