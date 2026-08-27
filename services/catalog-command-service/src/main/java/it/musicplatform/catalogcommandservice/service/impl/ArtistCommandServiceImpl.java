package it.musicplatform.catalogcommandservice.service.impl;

import it.musicplatform.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.musicplatform.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.musicplatform.catalogcommandservice.exception.BadRequestException;
import it.musicplatform.catalogcommandservice.exception.NotFoundException;
import it.musicplatform.catalogcommandservice.mapper.ArtistMapper;
import it.musicplatform.catalogcommandservice.model.Artist;
import it.musicplatform.catalogcommandservice.repository.ArtistRepository;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistCommandServiceImpl implements ArtistCommandService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    // private final EventPublisher eventPublisher; // TODO

    private void validateArtistRequest(ArtistCreationRequestDTO creationRequestDTO) {
        if (creationRequestDTO.getFoundationDate() == null) {
            log.warn("Creating artist with missing foundation date.");
            throw new BadRequestException("Foundation date is null.");
        }

        // Constraint [V.Artist.registrato_dopo_fondazione]
        if (creationRequestDTO.getFoundationDate().isAfter(LocalDate.now())) {
            log.warn("Artist foundation date is in the future.");
            throw new BadRequestException("Foundation date should be before registration date.");
        }
    }

    public Artist findByIdOrThrow(UUID id) {
       return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id + "."));
    }

    @Override
    public ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO) {
        validateArtistRequest(creationRequestDTO);

        Artist artist = artistMapper.toEntity(creationRequestDTO);

        Artist savedArtist = artistRepository.save(artist);

        // TODO: Pubblicare l'evento "ArtistCreatedEvent" sul message broker (es. Kafka/RabbitMQ)
        // eventPublisher.publishArtistCreated(savedArtist.getId(), savedArtist.getName());

        return artistMapper.toSummaryResponse(savedArtist);
    }

    @Override
    public ArtistSummaryResponseDTO updateArtist(UUID artistId, ArtistCreationRequestDTO creationRequestDTO) {
        validateArtistRequest(creationRequestDTO);

        Artist artist = findByIdOrThrow(artistId);

        artist.setName(creationRequestDTO.getName());
        artist.setFoundationDate(creationRequestDTO.getFoundationDate());

        Artist updatedArtist = artistRepository.save(artist);
        return artistMapper.toSummaryResponse(updatedArtist);
    }
}
