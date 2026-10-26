package it.eufonica.authservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.authservice.dto.auth.SignUpRequestDTO;
import it.eufonica.authservice.security.CurrentUserProvider;
import it.eufonica.authservice.service.AuthService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/auth")
@RequiredArgsConstructor @Slf4j
public class AuthController {
    private final AuthService authService;
    private final CurrentUserProvider currentUser;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User signed up."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. invalid display name format, name too short or too long).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Display name is reserved (reason: RESERVED_DISPLAY_NAME) or already in use (reason: DISPLAY_NAME_ALREADY_IN_USE), or this subject has already signed up (reason: ALREADY_SIGNED_UP).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(value = "/signup", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpRequestDTO signUpRequest) {
        log.debug("SignUpRequestDTO: {}", signUpRequest);
        String sub = currentUser.getSub();

        authService.signUp(signUpRequest, sub);
    }
}
