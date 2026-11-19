package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.listen.ListenResponseDTO;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.web.dto.PageResponseDTO;

import java.util.UUID;

public interface ListenService {
    /**
     * Retrieves the listen performed by the caller, sorted by
     * {@code timestamp}.
     *
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     *
     * @return a paginated list of flattened dto, each containing the most
     * essential information of the {@code Listen} and the {@code Song}
     */
    PageResponseDTO<ListenResponseDTO> getListenHistory(Integer pageNumber, Integer pageSize);

    /**
     * Registers a listen of the given song by the current user.
     *
     * <p>The listen is silently ignored (no-op) if either of the following
     * holds: {@code secPlayed} does not meet the minimum play threshold for
     * the song (based on its duration), or the current user has listened to
     * the song too recently (within the configured cooldown window).
     *
     * <p>On success, a {@code SongListenedEvent} is published.
     *
     * @param songId the id of the song being listened to
     * @param secPlayed the number of seconds played during this listen
     *
     * @throws BadRequestException if {@code secPlayed} is greater than the
     * song's duration
     * @throws NotFoundException if no song exists with the given id
     */
    void listen(UUID songId, int secPlayed);
}
