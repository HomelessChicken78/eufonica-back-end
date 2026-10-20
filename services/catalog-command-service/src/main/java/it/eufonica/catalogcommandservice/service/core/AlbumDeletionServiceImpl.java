package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.event.album.AlbumDeletedEvent;
import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.outbox.OutboxEventPublisherService;
import it.eufonica.catalogcommandservice.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AlbumDeletionServiceImpl implements AlbumDeletionService {
    // Service
    private final OutboxEventPublisherService eventPublisher;

    // Repository
    private final AlbumRepository albumRepository;

    @Value("${ALBUM_TOPIC_NAME:album.events}")
    private String albumTopicName;

    @Override
    public void deleteAlbum(Album album) {
        album.getArtists().clear();
        album.getSongs().clear();

        albumRepository.deleteById(album.getId());

        eventPublisher.publish(album.getId().toString(), "AlbumDeletedEvent",
                albumTopicName, new AlbumDeletedEvent(album.getVersion(), album.getId()));

        log.info("Removing album. albumId={}", album.getId());
    }
}
