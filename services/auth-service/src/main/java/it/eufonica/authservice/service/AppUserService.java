package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AppUserService {
    List<AppUserShortResponseDTO> findAll(int pageNumber, int pageSize);
    AppUserFullResponseDTO findById(UUID userId);
    AppUserFullResponseDTO findSelf();
}
