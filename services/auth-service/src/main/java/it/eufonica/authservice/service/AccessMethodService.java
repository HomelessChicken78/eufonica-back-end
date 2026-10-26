package it.eufonica.authservice.service;

import it.eufonica.authservice.model.AppUser;

public interface AccessMethodService {
    /**
     * Retrieves the identity provider's subject (sub) associated with the given user.
     * Used to recover the Cognito sub from a user entity when it is not available
     * from the current request context (e.g. when acting on behalf of another user).
     *
     * @param user the user whose access method is looked up
     *
     * @return the provider user id (the Cognito sub) linked to the user
     * @throws IllegalStateException if the user has no associated access method
     * (should not normally happen, since an AppUser is only ever created together
     * with an AccessMethod during sign-up)
     */
    String getSubFromUser(AppUser user);
}
