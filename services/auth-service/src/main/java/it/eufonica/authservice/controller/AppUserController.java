package it.eufonica.authservice.controller;

import it.eufonica.authservice.dto.appuser.AppUserFiltersDTO;
import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.authservice.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/users")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<AppUserShortResponseDTO> findAll(@ModelAttribute @Valid AppUserFiltersDTO filters,
                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "${USER_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        return appUserService.findAll(filters, pageNumber, pageSize);
    }

    @GetMapping(value = "/me", produces =  APPLICATION_JSON_VALUE)
    public AppUserFullResponseDTO findSelf() {
        return appUserService.findSelf();
    }

    @GetMapping(value = "/{userId}", produces =  APPLICATION_JSON_VALUE)
    public AppUserFullResponseDTO findById(@PathVariable UUID userId) {
        return appUserService.findById(userId);
    }
}
