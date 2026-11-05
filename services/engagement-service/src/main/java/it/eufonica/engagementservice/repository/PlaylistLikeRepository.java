package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, UUID> {
    List<PlaylistLike> findByUserId(UUID userId);

    void deleteByPlaylistId(UUID playlistId);
}
