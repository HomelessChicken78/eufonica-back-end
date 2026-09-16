package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.AlbumRead;
import it.eufonica.catalogqueryservice.model.ArtAlbumRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtAlbumRepository extends JpaRepository<ArtAlbumRead, UUID> {
}
