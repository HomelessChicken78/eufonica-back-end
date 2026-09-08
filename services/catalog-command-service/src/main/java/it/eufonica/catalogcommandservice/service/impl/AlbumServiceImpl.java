package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AlbumServiceImpl implements AlbumService {
    @Override
    public AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request) {
        return null;
    }

    @Override
    public AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumCreationRequestDTO request) {
        return null;
    }

    @Override
    public void deleteAlbum(UUID albumId) {
    }

    @Override
    public AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId) {
        return null;
    }

    @Override
    public AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId) {
        return null;
    }
}
