package it.musicplatform.catalogcommandservice.service.impl;

import it.musicplatform.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.musicplatform.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.musicplatform.catalogcommandservice.repository.ArtistRepository;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor
public class ArtistCommandServiceImpl implements ArtistCommandService {
    private final ArtistRepository artistRepository;
    // private final EventPublisher eventPublisher; // TODO

    @Override
    public ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO) {
        return null;
    }

    @Override
    public ArtistSummaryResponseDTO updateArtist(UUID artistId, ArtistCreationRequestDTO creationRequestDTO) {
        return null;
    }
}
