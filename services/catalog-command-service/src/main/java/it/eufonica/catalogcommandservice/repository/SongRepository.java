package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {
    boolean existsByTitleAndArtistOwner(String title, Artist artistOwner);

    @Query("""
        SELECT COUNT(s) > 0
        FROM Song s
        JOIN s.creditedArtists a
        WHERE a = :artist
        AND s.publishedDate IS NOT NULL
        AND s.publishedDate < :foundationDate
    """)
    boolean existsSongConflictingWithFoundationDate(@Param("artist") Artist artist, @Param("foundationDate") LocalDate foundationDate);
}
