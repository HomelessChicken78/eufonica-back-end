package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.SignUpRequestDTO;
import it.eufonica.authservice.exception.client.ConflictException;
import it.eufonica.authservice.mapper.AppUserMapper;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import it.eufonica.authservice.repository.AppUserRepository;
import it.eufonica.authservice.security.CognitoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AuthServiceImpl implements AuthService {
    // Repositories
    private final AppUserRepository userRepository;
    private final AccessMethodRepository accessMethodRepository;

    // Auth
    private final CognitoUserService cognitoUserService;

    // Mapper & Utilities
    private final AppUserMapper userMapper;

    @Override
    public void signUp(SignUpRequestDTO request, String sub) {
        if (accessMethodRepository.existsByProviderNameAndProviderUserId("cognito", sub))
            throw new ConflictException("You have already signed up. Try to login instead.");

        if (userRepository.existsByDisplayName(request.getDisplayName()))
            throw new ConflictException(String.format("The username %s is already in use. Try a different one.", request.getDisplayName()));

        AppUser user = userRepository.save(userMapper.toEntity(request));
        accessMethodRepository.save(new AccessMethod(null, "cognito", sub, user));

        cognitoUserService.addToUserGroup(sub);

        log.info("User {} has been signed up.", user.getDisplayName());
    }
}
