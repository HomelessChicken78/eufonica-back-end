package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
}
