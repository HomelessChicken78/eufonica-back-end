package it.eufonica.authservice.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.authservice.dto.auth.SignUpRequestDTO;
import it.eufonica.authservice.security.CurrentUserProvider;
import it.eufonica.authservice.service.AuthService;
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
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. invalid display name format, name too short or too long)."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified."),
            @ApiResponse(responseCode = "409", description = "Display name is reserved or already in use, or this subject has already signed up.")
    })
    @PostMapping(value = "/signup", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpRequestDTO signUpRequest) {
        log.debug("SignUpRequestDTO: {}", signUpRequest);
        String sub = currentUser.getSub();

        authService.signUp(signUpRequest, sub);
    }
}
