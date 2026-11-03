package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<PlaylistEntry, UUID> {
}
