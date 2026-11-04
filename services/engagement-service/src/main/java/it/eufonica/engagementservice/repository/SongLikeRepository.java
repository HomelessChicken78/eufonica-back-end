package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.SongLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SongLikeRepository extends JpaRepository<SongLike, UUID> {
    void deleteBySongId(UUID songId);
}
