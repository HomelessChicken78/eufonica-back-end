package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Listen;
import it.eufonica.engagementservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ListenRepository extends JpaRepository<Listen, UUID> {
    void deleteBySongId(UUID songId);

    Optional<Listen> findFirstByUserAndSongOrderByTimestampDesc(AppUser user, Song song);
}
