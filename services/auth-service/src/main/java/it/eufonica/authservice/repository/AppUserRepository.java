package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    boolean existsByDisplayName(String displayName);

    Page<AppUser> findAll(Specification<AppUser> spec, Pageable page);
}
