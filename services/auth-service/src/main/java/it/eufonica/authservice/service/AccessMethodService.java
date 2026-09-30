package it.eufonica.authservice.service;

import it.eufonica.authservice.model.AppUser;

public interface AccessMethodService {
    String getSubFromUser(AppUser user);
}
