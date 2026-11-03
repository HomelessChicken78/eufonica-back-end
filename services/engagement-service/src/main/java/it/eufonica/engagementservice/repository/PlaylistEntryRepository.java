package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, UUID> {
    List<PlaylistEntry> findByPlaylist(Playlist playlist);
}
