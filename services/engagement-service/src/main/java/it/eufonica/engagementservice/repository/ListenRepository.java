package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Listen;
import it.eufonica.engagementservice.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListenRepository extends JpaRepository<Listen, UUID> {
    void deleteBySongId(UUID songId);

    Optional<Listen> findFirstByUserAndSongOrderByTimestampDesc(AppUser user, Song song);

    @Query("""
    SELECT l
    FROM Listen l
    WHERE l.user = :user
    ORDER BY l.timestamp ASC
    """)
    Page<Listen> findHistory(@Param("user") AppUser user, Pageable page);
}
