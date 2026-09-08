package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SongCommandService {
    // TODO ownerId should be taken from the jwt.
    SongResponseDTO publishSong(PublishSongRequestDTO request, UUID ownerId, MultipartFile audioFile);

    SongResponseDTO addCreditedArtist(UUID idSong, UUID idArtist);

    SongResponseDTO removeCreditedArtist(UUID idSong, UUID idArtist);
}
