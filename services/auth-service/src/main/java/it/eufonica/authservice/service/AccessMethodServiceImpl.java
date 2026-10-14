package it.eufonica.authservice.service;

import it.eufonica.authservice.exception.ErrorReason;
import it.eufonica.authservice.exception.server.InternalServerErrorException;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class AccessMethodServiceImpl implements AccessMethodService {
    private final AccessMethodRepository accessMethodRepository;

    @Override
    public String getSubFromUser(AppUser user) {
        return accessMethodRepository.findByUser(user)
                .orElseThrow(() ->
                        // This shouldn't normally happen: each user must be created after registering (so AccessMethod is always created)
                        new InternalServerErrorException("User " + user.getId() + " has no associated access method.",
                                ErrorReason.INTERNAL_ERROR))
                .getProviderUserId();
    }
}
