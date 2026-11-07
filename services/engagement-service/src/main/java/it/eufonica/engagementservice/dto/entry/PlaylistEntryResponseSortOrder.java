package it.eufonica.engagementservice.dto.entry;

/**
 * All values in this enum must follow the following naming convention:
 * <p>{@code {song|entry}_{attributeName}_{asc|desc}}
 *
 * <ul>
 * <li>{@code entry_} can be omitted.</li>
 * <li>{@code _asc} can be omitted.</li>
 * </ul>
 */
public enum PlaylistEntryResponseSortOrder {
    POSITION,
    SONG_TITLE_ASC,
    SONG_TITLE_DESC,
    SONG_ARTIST_OWNER_NAME_ASC,
    SONG_ARTIST_OWNER_NAME_DESC
}
