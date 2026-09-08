package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.album.*;
import it.eufonica.catalogcommandservice.model.Album;

import java.util.UUID;

public interface AlbumCommandService {


    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumCreationRequestDTO request);

    void deleteAlbum(UUID albumId);

    AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId);

    AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId);
}
