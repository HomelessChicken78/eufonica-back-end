package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    boolean existsByName(String name);
}
