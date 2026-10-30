package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PresignedUrlResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.song.SongShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongResponseSortOrder;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;

import java.util.UUID;

public interface SongQueryService {
    /**
     * Finds a song by its id.
     *
     * @param songId the unique id of the song
     * @return the full DTO representing the song
     * @throws NotFoundException if no song exists with the given id
     */
    SongFullResponseDTO findSongById(UUID songId);

    /**
     * Searches songs matching the given filters.
     *
     * @param filters the search filters to use
     * @param sortOrder the criteria to sort the result
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     * @return the paginated search result
     */
    PageResponseDTO<SongShortResponseDTO> searchSongs(SongSearchFiltersDTO filters, SongResponseSortOrder sortOrder,
                                                      Integer pageNumber, Integer pageSize);

    /**
     * Generates a short-lived presigned URL for downloading or streaming the audio file of a song.
     *
     * @param songId the unique id of the song
     *
     * @return a DTO containing the presigned URL and its expiration instant
     * @throws NotFoundException if no song exists with the given id
     * @throws ForbiddenException if the caller does not have the "user" role
     */
    PresignedUrlResponseDTO getAudioUrl(UUID songId);
}