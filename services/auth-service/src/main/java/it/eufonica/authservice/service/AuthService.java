package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.SignUpRequestDTO;

public interface AuthService {
    void signUp(SignUpRequestDTO request, String sub);
}
