package it.eufonica.authservice.exception;

import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.ErrorReason;

public enum ArtistCreationUpstreamErrorReason implements ErrorReason {
    ARTIST_NAME_ALREADY_EXISTS;

    /**
     * Attempts to convert the given upstream reason string into one of this enum's constants.
     * If it doesn't match any, falls back to a {@link CommonErrorReason} constant instead, and if
     * that doesn't match either, returns {@link CommonErrorReason#UNEXPECTED_ERROR}.
     *
     * @param reason the raw reason string received from the upstream service
     *
     * @return the matching {@code ArtistCreationUpstreamErrorReason} or {@code CommonErrorReason}
     * constant, or {@code CommonErrorReason.UNEXPECTED_ERROR} if neither matches
     */
    public static ErrorReason fromUpstream(String reason) {
        try {
            return valueOf(reason);
        } catch (IllegalArgumentException e) {
            try {
                return CommonErrorReason.valueOf(reason);
            } catch (IllegalArgumentException e2) {
                return CommonErrorReason.UNEXPECTED_ERROR;
            }
        }
    }
}
