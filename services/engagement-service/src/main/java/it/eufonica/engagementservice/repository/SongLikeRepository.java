package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Song;
import it.eufonica.engagementservice.model.SongLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongLikeRepository extends JpaRepository<SongLike, UUID> {
    void deleteBySongId(UUID songId);

    boolean existsBySongAndUser(Song song, AppUser appUser);
}
