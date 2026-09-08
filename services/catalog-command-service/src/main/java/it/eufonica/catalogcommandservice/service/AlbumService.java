package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.album.*;

import java.util.UUID;

public interface AlbumService {
    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumCreationRequestDTO request);

    void deleteAlbum(UUID albumId);

    AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId);

    AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId);
}
