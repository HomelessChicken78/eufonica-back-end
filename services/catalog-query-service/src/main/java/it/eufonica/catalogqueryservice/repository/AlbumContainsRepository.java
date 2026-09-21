package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.AlbumContainsRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumContainsRepository extends JpaRepository<AlbumContainsRead, UUID> {
    void deleteByAlbumId(UUID albumId);

    void deleteByAlbumIdAndSongId(UUID albumId, UUID songId);

    boolean existsByAlbumIdAndSongId(UUID albumId, UUID songId);
}
