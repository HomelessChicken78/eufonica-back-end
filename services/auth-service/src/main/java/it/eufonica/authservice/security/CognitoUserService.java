package it.eufonica.authservice.security;

import java.util.UUID;

public interface CognitoUserService {
    /**
     * Set the Cognito attribute "custom:artist_id" for a Cognito user
     * to the requested artist id.
     * This allows to know what Artist the user is affiliated to
     *
     * @param sub The Cognito sub associated to the AppUser
     * @param artistId The id of the artist affiliated to the user
     */
    void setAffiliatedArtist(String sub, UUID artistId);

    /**
     * Removes the Cognito attribute "custom:artist_id" from a Cognito user.
     * Useful if the AppUser is no longer affiliated to the Artist
     *
     * @param sub The Cognito sub associated to the AppUser
     */
    void clearAffiliatedArtist(String sub);
}
