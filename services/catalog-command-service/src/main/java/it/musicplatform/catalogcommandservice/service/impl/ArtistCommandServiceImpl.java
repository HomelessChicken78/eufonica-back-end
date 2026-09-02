package it.musicplatform.catalogcommandservice.service.impl;

import it.musicplatform.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.musicplatform.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.musicplatform.catalogcommandservice.exception.client.BadRequestException;
import it.musicplatform.catalogcommandservice.exception.client.NotFoundException;
import it.musicplatform.catalogcommandservice.mapper.ArtistMapper;
import it.musicplatform.catalogcommandservice.model.Artist;
import it.musicplatform.catalogcommandservice.repository.ArtistRepository;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistCommandServiceImpl implements ArtistCommandService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    // private final EventPublisher eventPublisher; // TODO

    /**
     * Validates the artist creation/update request.
     *
     * @param creationRequestDTO the artist creation/update request to validate
     * @throws BadRequestException if the foundation date is missing or is in the future
     * or if the artist's name is already in use
     */
    private void validateArtistRequest(ArtistCreationRequestDTO creationRequestDTO) {
        if (creationRequestDTO.getFoundationDate() == null)
            throw new BadRequestException("Foundation date is null.");

        // Constraint [V.Artist.registrato_dopo_fondazione]
        if (creationRequestDTO.getFoundationDate().isAfter(LocalDate.now()))
            throw new BadRequestException("Foundation date should be before registration date.");

        // Check that the name doesn't already exist (unique)
        if (artistRepository.existsByName(creationRequestDTO.getName()))
            throw new BadRequestException("Artist with name " + creationRequestDTO.getName() + " already exists.");
    }

    @Override
    public Artist findByIdOrThrow(UUID id) {
       return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id + "."));
    }

    @Override
    public ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO) {
        log.info("Creating artist with name {}.", creationRequestDTO.getName());
        validateArtistRequest(creationRequestDTO);

        Artist artist = artistMapper.toEntity(creationRequestDTO);
        artist.setRegistrationDate(LocalDateTime.now());

        Artist savedArtist = artistRepository.save(artist);

        // TODO: Pubblicare l'evento "ArtistCreatedEvent" sul message broker (es. Kafka/RabbitMQ)
        // eventPublisher.publishArtistCreated(savedArtist.getId(), savedArtist.getName());

        return artistMapper.toSummaryResponse(savedArtist);
    }

    @Override
    public ArtistSummaryResponseDTO updateArtist(UUID artistId, ArtistCreationRequestDTO creationRequestDTO) {
        log.info("Updating artist with id {}.", artistId);
        validateArtistRequest(creationRequestDTO);

        Artist artist = findByIdOrThrow(artistId);

        artist.setName(creationRequestDTO.getName());
        artist.setFoundationDate(creationRequestDTO.getFoundationDate());

        Artist updatedArtist = artistRepository.save(artist);
        return artistMapper.toSummaryResponse(updatedArtist);
    }
}
