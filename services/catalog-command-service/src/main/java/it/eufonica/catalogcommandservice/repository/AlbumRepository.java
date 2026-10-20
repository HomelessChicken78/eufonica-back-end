package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Set<Album> findBySongsContaining(Song toDelete);
}
