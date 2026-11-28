package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {
    boolean existsByTitleAndArtistOwner(String title, Artist artistOwner);

    boolean existsByCreditedArtistsContainingAndPublishedDateBeforeAndPublishedDateIsNotNull(
            Artist artist,
            @NotNull(message = "Foundation date is mandatory.") @PastOrPresent(message = "Foundation date can't be in the future.")
            LocalDate newFoundationDate
    );
}
