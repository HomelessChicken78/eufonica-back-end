package it.eufonica.authservice.repository;

import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRequestRepository extends JpaRepository<ArtistRequest, UUID> {
    boolean existsByRequestingUserAndStatus(AppUser requestingUser, ArtistRequest.RequestStatus status);

    Optional<ArtistRequest> findOneByRequestedNameAndStatus(String requestedName, ArtistRequest.RequestStatus status);

    default Optional<ArtistRequest> findOneAcceptedByRequestedName(String requestedName) {
        return findOneByRequestedNameAndStatus(requestedName, ArtistRequest.RequestStatus.ACCEPTED);
    }

    Page<ArtistRequest> findAll(Specification<ArtistRequest> spec, Pageable page);
}
