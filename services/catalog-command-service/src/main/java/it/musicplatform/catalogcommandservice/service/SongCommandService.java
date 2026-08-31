package it.musicplatform.catalogcommandservice.service;

import it.musicplatform.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.musicplatform.catalogcommandservice.dto.song.SongResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SongCommandService {
    // TODO ownerId should be taken from the jwt.
    SongResponseDTO publishSong(PublishSongRequestDTO request, UUID ownerId, MultipartFile audioFile);
}
