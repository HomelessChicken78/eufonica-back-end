package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {
    boolean existsByTitleAndArtistOwner(String title, Artist artistOwner);
}
