package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.SongCreditRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongCreditRepository extends JpaRepository<SongCreditRead, UUID> {
    void deleteBySongId(UUID songId);

    void deleteBySongIdAndArtistId(UUID songId, UUID artistId);

    boolean existsBySongIdAndArtistId(UUID songId, UUID artistId);
}
