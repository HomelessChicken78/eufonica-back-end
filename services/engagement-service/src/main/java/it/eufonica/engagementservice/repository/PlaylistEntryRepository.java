package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, UUID> {
    List<PlaylistEntry> findByPlaylist(Playlist playlist);

    @Query("""
    SELECT MAX(e.position) FROM PlaylistEntry e
    WHERE e.playlist = :playlist
    """)
    Optional<Long> findLastPosition(@Param("playlist") Playlist playlist);

    void deleteByPlaylistId(UUID playlistId);

    Optional<PlaylistEntry> findByIdAndPlaylist(UUID entryId, Playlist playlist);
}
