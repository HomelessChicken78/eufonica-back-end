    // noinspection JSUnusedGlobalSymbols

    /**
     * Cognito Pre Token Generation trigger (V2).
     *
     * Reads the "custom:artist_id" user attribute (set by the Auth Service
     * via AdminUpdateUserAttributes when an artist request is approved) and,
     * if present, adds it as an "artist_id" claim on the generated ACCESS
     * token. Without this, custom attributes only end up in the ID token.
     *
     * Trigger source expected: "TokenGeneration_HostedAuth" / "TokenGeneration_RefreshTokens" etc.
     * Trigger version required: V2_0 (only V2 supports overriding the access token).
     */
    export const handler = async (event) => {
        // Get the artist id from Cognito. The object "request" contains all the Cognito user's data
        const artistId = event.request.userAttributes["custom:artist_id"];

        /* If it is undefined, it means there is no affiliated artist, so we don't need to add any
        claim to the access token. If it is not undefined, we add the claim to the access token.*/
        if (artistId) {
            /* The object "response" contains various other sub-objects, which can be used to
            add or override claims and scopes. If not touched, these fields are usually empty,
            which is why we can simply override the whole sub-object we specifically need. */
            event.response.claimsAndScopeOverrideDetails = {
                // This means "I want to override something about the access token"
                accessTokenGeneration: {
                    // This means "I want to override/add claims
                    claimsToAddOrOverride: {
                        artist_id: artistId
                    }
                }
            };
        }

        return event;
    };