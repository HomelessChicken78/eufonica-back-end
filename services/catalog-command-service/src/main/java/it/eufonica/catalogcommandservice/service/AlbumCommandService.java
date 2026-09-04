package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;

import java.util.UUID;

public interface AlbumCommandService {
    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    void addSongToAlbum(UUID albumId, UUID songId);

    void removeSongFromAlbum(UUID albumId, UUID songId);
}
