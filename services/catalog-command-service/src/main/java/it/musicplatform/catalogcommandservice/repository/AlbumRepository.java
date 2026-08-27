package it.musicplatform.catalogcommandservice.repository;

import it.musicplatform.catalogcommandservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
}
