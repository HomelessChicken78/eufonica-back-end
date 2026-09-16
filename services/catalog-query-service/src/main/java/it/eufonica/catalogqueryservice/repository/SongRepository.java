package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.SongRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SongRepository extends JpaRepository<SongRead, UUID> {
    List<SongRead> findByArtistOwnerId(UUID artistOwnerId);
}
