package it.eufonica.authservice.controller;

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

    @PostMapping(value = "/signup", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpRequestDTO signUpRequest) {
        log.debug("SignUpRequestDTO: {}", signUpRequest);
        String sub = currentUser.getSub();

        authService.signUp(signUpRequest, sub);
    }
}
