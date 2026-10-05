package it.eufonica.authservice.consumer;

import it.eufonica.authservice.event.artist.ArtistCreatedEvent;
import it.eufonica.authservice.event.artist.ArtistUpdatedEvent;
import it.eufonica.authservice.mapper.ArtistMapper;
import it.eufonica.authservice.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistEventConsumer {
    // Utility & Mappers
    private final ObjectMapper objectMapper;
    private final ArtistMapper artistMapper;

    // Repositories
    private final ArtistRepository artistRepository;

    @KafkaListener(topics = "${ARTIST_TOPIC_NAME:artist.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for artist. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "ArtistCreatedEvent" ->
                    handleArtistCreation(eventId, objectMapper.readValue(payload, ArtistCreatedEvent.class));
            case "ArtistUpdatedEvent" ->
                    handleArtistUpdate(eventId, objectMapper.readValue(payload, ArtistUpdatedEvent.class));
            default -> log.warn("Unknown event type received. eventType={}", eventType);
        }
    }

    /**
     * Handles the creation of a new artist after an ArtistCreatedEvent is received.
     *
     * @param eventId The id of the event used for logging
     * @param event The deserialized event's payload
     */
    // We don't need to check the version since all the events for the same aggregate end up in the same partistion.
    /*
    We don't need to check if the event is already processed since processing it twice with only one mutable field
    (name) causes no harm.
    */
    public void handleArtistCreation(UUID eventId, ArtistCreatedEvent event) {
        log.debug("Creating artist for event {}.", eventId);

        artistRepository.save(artistMapper.toEntity(event));
    }

    /*
     * We don't need to check the version since all the events for the same aggregate end up in the same partition.
     */
    /*
     * We don't need to check if the event is already processed since processing it twice with only one mutable field
     * (name) causes no harm.
     */
    /**
     * Handles the update of an artist after an ArtistUpdatedEvent is received.
     *
     * @param eventId The id of the event used for logging
     * @param event The deserialized event's payload
     */
    public void handleArtistUpdate(UUID eventId, ArtistUpdatedEvent event) {
        artistRepository.findById(event.getId()).ifPresentOrElse(
                existing -> {
                    existing.setName(event.getName());
                    artistRepository.save(existing);
                    log.debug("Updated name for artist {} after event {}.", event.getId(), eventId);
                },
                () -> log.warn("Received update for an artist not present in the projection. eventId={}, artistId={}",
                        eventId, event.getId())
        );
    }
}

