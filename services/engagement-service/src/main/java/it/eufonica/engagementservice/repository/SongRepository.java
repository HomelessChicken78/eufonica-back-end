package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {

}
