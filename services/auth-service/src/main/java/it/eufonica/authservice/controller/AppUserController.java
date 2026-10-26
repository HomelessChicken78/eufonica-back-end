package it.eufonica.authservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.authservice.dto.appuser.AppUserFiltersDTO;
import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.authservice.service.AppUserService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/users")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin (reason: NOT_AN_ADMIN).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<AppUserShortResponseDTO> findAll(@ModelAttribute @Valid AppUserFiltersDTO filters,
                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "${USER_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        return appUserService.findAll(filters, pageNumber, pageSize);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The caller's own user profile."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(value = "/me", produces =  APPLICATION_JSON_VALUE)
    public AppUserFullResponseDTO findSelf() {
        return appUserService.findSelf();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found."),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin (reason: NOT_AN_ADMIN).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The user does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(value = "/{userId}", produces =  APPLICATION_JSON_VALUE)
    public AppUserFullResponseDTO findById(@PathVariable UUID userId) {
        return appUserService.findById(userId);
    }
}
