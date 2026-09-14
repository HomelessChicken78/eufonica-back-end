package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.catalogcommandservice.event.artist.ArtistCreatedEvent;
import it.eufonica.catalogcommandservice.event.artist.ArtistUpdatedEvent;
import it.eufonica.catalogcommandservice.exception.client.BadRequestException;
import it.eufonica.catalogcommandservice.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
import it.eufonica.catalogcommandservice.mapper.ArtistMapper;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.outbox.OutboxEvent;
import it.eufonica.catalogcommandservice.outbox.OutboxService;
import it.eufonica.catalogcommandservice.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistCommandServiceImpl implements ArtistCommandService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper; // Used to serialize the JSON payload

    @Value("${ARTIST_TOPIC_NAME:artist.events}")
    private String artistTopicName;

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
    }

    @Override
    public Artist findByIdOrThrow(UUID id) {
       return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id + "."));
    }

    /**
     * Creates and persists the outbox event for a newly created artist.
     *
     * @param artist the persisted artist to create the event from
     * @throws InternalServerErrorException if the event payload cannot be serialized
     */
    private void publishArtistCreatedEvent(Artist artist) {
        ArtistCreatedEvent event = artistMapper.toCreationEvent(artist);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new InternalServerErrorException("Failed to serialize ArtistCreatedEvent.");
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(artist.getId().toString())
                .eventType("ArtistCreatedEvent")
                .topic(artistTopicName)
                .payload(payload)
                .build();

        outboxService.save(outboxEvent);
        log.debug("Put ArtistCreatedEvent in outbox. artistId={}", artist.getId());
    }

    /**
     * Creates and persists the outbox event for an updated artist.
     *
     * @param artist the updated artist to create the event from
     * @throws InternalServerErrorException if the event payload cannot be serialized
     */
    private void publishArtistUpdatedEvent(Artist artist) {
        ArtistUpdatedEvent event = artistMapper.toUpdatingEvent(artist);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new InternalServerErrorException("Failed to serialize ArtistUpdatedEvent.");
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(artist.getId().toString())
                .eventType("ArtistUpdatedEvent")
                .topic(artistTopicName)
                .payload(payload)
                .build();

        outboxService.save(outboxEvent);
        log.debug("Put ArtistUpdatedEvent in outbox. artistId={}", artist.getId());
    }

    @Override
    public ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO) {
        log.info("Creating artist with name {}.", creationRequestDTO.getName());
        validateArtistRequest(creationRequestDTO);

        // Check that the name doesn't already exist (unique)
        if (artistRepository.existsByName(creationRequestDTO.getName()))
            throw new BadRequestException("Artist with name " + creationRequestDTO.getName() + " already exists.");

        Artist artist = artistMapper.toEntity(creationRequestDTO);
        artist.setRegistrationDate(LocalDateTime.now());

        Artist savedArtist = artistRepository.save(artist);

        publishArtistCreatedEvent(artist);

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

        publishArtistUpdatedEvent(artist);

        return artistMapper.toSummaryResponse(updatedArtist);
    }
}
