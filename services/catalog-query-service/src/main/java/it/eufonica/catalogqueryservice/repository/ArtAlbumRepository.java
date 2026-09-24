package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.ArtAlbumRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArtAlbumRepository extends JpaRepository<ArtAlbumRead, UUID> {
    void deleteByAlbumId(UUID albumId);

    List<ArtAlbumRead> findByAlbumId(UUID albumId);
}
