package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.appuser.AppUserFiltersDTO;
import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;

import java.util.UUID;

public interface AppUserService {
    /**
     * Searches application users matching the given filters, paginated.
     * Restricted to admins.
     *
     * @param filters the filters to apply to the search
     * @param pageNumber the 1-based page number to retrieve
     * @param pageSize the number of results per page
     *
     * @return a page of users matching the given filters
     * @throws ForbiddenException if the caller is not an admin
     * @throws BadRequestException if pageNumber or pageSize is less than 1
     */
    PageResponseDTO<AppUserShortResponseDTO> findAll(AppUserFiltersDTO filters, int pageNumber, int pageSize);

    /**
     * Retrieves the full profile of the user with the given id.
     * Restricted to admins.
     *
     * @param userId the id of the user to retrieve
     *
     * @return the full profile of the requested user
     * @throws ForbiddenException if the caller is not an admin
     * @throws NotFoundException if no user with the given id exists
     */
    AppUserFullResponseDTO findById(UUID userId);

    /**
     * Retrieves the full profile of the currently authenticated user.
     *
     * @return the full profile of the caller
     * @throws it.eufonica.exception.client.UnauthorizedException if no authenticated subject could be identified
     */
    AppUserFullResponseDTO findSelf();
}
