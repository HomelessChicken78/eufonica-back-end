package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.auth.SignUpRequestDTO;

public interface AuthService {
    void signUp(SignUpRequestDTO request, String sub);
}
