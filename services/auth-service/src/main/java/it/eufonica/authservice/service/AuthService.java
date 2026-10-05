package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.auth.SignUpRequestDTO;
import it.eufonica.authservice.exception.client.ConflictException;

public interface AuthService {
    /**
     * Registers the currently authenticated Cognito user as an {@code AppUser},
     * creating the associated {@code AccessMethod} and adding them to the default
     * user group on Cognito.
     *
     * @param request the display name and optional names chosen by the user
     * @param sub the Cognito sub of the currently authenticated user
     *
     * @throws ConflictException if the display name is the reserved current-user
     * keyword, if this Cognito user has already signed up, or if the display
     * name is already in use by another user
     */
    void signUp(SignUpRequestDTO request, String sub);
}
