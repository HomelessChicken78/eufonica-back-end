package it.musicplatform.catalogcommandservice.repository;

import it.musicplatform.catalogcommandservice.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
}
