package it.eufonica.engagementservice.repository;

import it.eufonica.engagementservice.model.Listen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListenRepository extends JpaRepository<Listen, UUID> {
    void deleteBySongId(UUID songId);
}
