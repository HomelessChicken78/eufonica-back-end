package it.musicplatform.catalogcommandservice.service;

import it.musicplatform.catalogcommandservice.dto.album.*;

import java.util.UUID;

public interface AlbumCommandService {
    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    void addSongToAlbum(UUID albumId, UUID songId);

    void removeSongFromAlbum(UUID albumId, UUID songId);
}
