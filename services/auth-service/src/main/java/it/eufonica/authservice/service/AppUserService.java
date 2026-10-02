package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.appuser.AppUserFiltersDTO;
import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.common.PageResponseDTO;

import java.util.UUID;

public interface AppUserService {
    PageResponseDTO<AppUserShortResponseDTO> findAll(AppUserFiltersDTO filters, int pageNumber, int pageSize);
    AppUserFullResponseDTO findById(UUID userId);
    AppUserFullResponseDTO findSelf();
}
