package it.eufonica.catalogqueryservice.repository;

import it.eufonica.catalogqueryservice.model.AlbumRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<AlbumRead, UUID> {
    Page<AlbumRead> findAll(Specification<AlbumRead> spec, Pageable pageable);
}
