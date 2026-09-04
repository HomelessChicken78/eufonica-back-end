package it.musicplatform.catalogcommandservice.repository;

import it.musicplatform.catalogcommandservice.model.Artist;
import it.musicplatform.catalogcommandservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {
    boolean existsByTitleAndArtistOwner(String title, Artist artistOwner);
}
