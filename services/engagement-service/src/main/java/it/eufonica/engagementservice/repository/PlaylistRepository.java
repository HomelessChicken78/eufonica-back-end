package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    Page<Playlist> findAll(Specification<Playlist> spec, Pageable page);
}
