package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.album.*;
import it.eufonica.catalogcommandservice.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.model.Album;

import java.util.UUID;

public interface AlbumCommandService {
    /**
     * Finds an album by its id.
     *
     * @param id the unique id of the album
     * @return the album associated with the given id
     * @throws NotFoundException if no album exists with the given id
     */
    Album findByIdOrThrow(UUID id);

    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumCreationRequestDTO request);

    void deleteAlbum(UUID albumId);

    AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId);

    AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId);
}
