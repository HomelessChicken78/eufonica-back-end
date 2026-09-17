package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.event.song.CreditedArtistAddedEvent;
import it.eufonica.catalogqueryservice.event.song.CreditedArtistRemovedEvent;
import it.eufonica.catalogqueryservice.event.song.SongCreatedEvent;
import it.eufonica.catalogqueryservice.processing.EventProcessingService;
import it.eufonica.catalogqueryservice.repository.SongCreditRepository;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import it.eufonica.catalogqueryservice.versionchecker.VersionChecker;
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
public class SongEventConsumer {
    private final ObjectMapper objectMapper;
    private final VersionChecker versionChecker;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final SongRepository songRepository;
    private final SongCreditRepository songCreditRepository;

    @KafkaListener(topics = "${SONG_TOPIC_NAME:song.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "SongCreatedEvent" -> handleSongCreation(eventId, objectMapper.readValue(payload, SongCreatedEvent.class));
            case "CreditedArtistAddedEvent" -> handleCreditedArtistAdded(eventId, objectMapper.readValue(payload, CreditedArtistAddedEvent.class));
            case "CreditedArtistRemovedEvent" -> handleCreditedArtistRemoved(eventId, objectMapper.readValue(payload, CreditedArtistRemovedEvent.class));
            default -> log.warn("Unknown event type received. eventType={}", eventType);
        }
    }

    /**
     * Handles the creation of a new song after a SongCreatedEvent is received.
     * If the song already exists, assume the event is duplicated and ignores it.
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleSongCreation(UUID eventId, SongCreatedEvent event) {

    }

    /**
     * Handles the attaching of a credited artist to a song.
     * Compares the current song's version against the event's version.
     * If the event's version is less than the song's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleCreditedArtistAdded(UUID eventId, CreditedArtistAddedEvent event) {

    }

    /**
     * Handles the detaching of a credited artist to a song.
     * Compares the current song's version against the event's version.
     * If the event's version is less than the song's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleCreditedArtistRemoved(UUID eventId, CreditedArtistRemovedEvent event) {

    }
}
