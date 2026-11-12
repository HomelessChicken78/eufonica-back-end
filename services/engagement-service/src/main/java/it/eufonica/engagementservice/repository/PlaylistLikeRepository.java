package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, UUID> {
    void deleteByPlaylistId(UUID playlistId);

    boolean existsByUserAndPlaylist(AppUser user, Playlist playlist);

    void deleteByUserAndPlaylist(AppUser appUser, Playlist found);
}
