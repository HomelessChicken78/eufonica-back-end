package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.appuser.AppUserFiltersDTO;
import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.authservice.exception.client.BadRequestException;
import it.eufonica.authservice.exception.client.NotFoundException;
import it.eufonica.authservice.mapper.AppUserMapper;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AppUserRepository;
import it.eufonica.authservice.security.CurrentUserProvider;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository userRepository;
    private final AppUserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    @Value("${USER_REQUEST_MAX_PAGE_SIZE:200}")
    private int maxPageSize;

    private AppUser findByIdOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with the given id (" + userId + ") does not exist."));
    }

    private Specification<AppUser> buildSpecification(AppUserFiltersDTO filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Display name (partial match)
            if (filters.getDisplayName() != null) {
                String escapedName = filters.getDisplayName()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("displayName")),
                        "%" + escapedName.toLowerCase() + "%", '\\'));
                log.trace("Added filter displayName LIKE %{}%.", escapedName);
            }

            // Has affiliated artist
            if (filters.getHasAffiliatedArtist() != null) {
                if (filters.getHasAffiliatedArtist())
                    predicates.add(criteriaBuilder.isNotNull(root.get("affiliatedArtist")));
                else
                    predicates.add(criteriaBuilder.isNull(root.get("affiliatedArtist")));
                log.trace("Added filter hasAffiliatedArtist = {}.", filters.getHasAffiliatedArtist());
            }

            // Registered since
            if (filters.getRegisteredSince() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationTimestamp"),
                        filters.getRegisteredSince().atStartOfDay()));
                log.trace("Added filter registrationTimestamp >= {}.", filters.getRegisteredSince().atStartOfDay());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponseDTO<AppUserShortResponseDTO> findAll(AppUserFiltersDTO filters, int pageNumber, int pageSize) {
        if (pageNumber < 1)
            throw new BadRequestException("pageNumber must be greater than or equal to 1.");
        if (pageSize < 1)
            throw new BadRequestException("pageSize must be greater than or equal to 1.");

        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        Specification<AppUser> spec = buildSpecification(filters);

        Page<AppUser> results = userRepository.findAll(spec, PageRequest.of(pageNumber - 1, pageSize));

        List<AppUserShortResponseDTO> content = results.stream()
                .map(userMapper::toShortResponse)
                .toList();

        return PageResponseDTO.<AppUserShortResponseDTO>builder()
                .content(content)
                .currentPage(pageNumber)
                .totalPages(results.getTotalPages())
                .totalElements(results.getTotalElements())
                .pageSize(pageSize)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AppUserFullResponseDTO findById(UUID userId) {
        AppUser user = findByIdOrThrow(userId);
        return userMapper.toFullResponse(user);
    }

    @Override
    public AppUserFullResponseDTO findSelf() {
        AppUser user = currentUserProvider.getCurrentUser();
        return userMapper.toFullResponse(user);
    }
}