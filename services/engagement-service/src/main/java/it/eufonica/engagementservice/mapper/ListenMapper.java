package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.dto.listen.ListenResponseDTO;
import it.eufonica.engagementservice.model.Listen;
import it.eufonica.engagementservice.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListenMapper {
    @Mapping(target = "songTitle", source = "song.title")
    @Mapping(target = "songId", source = "song.id")
    @Mapping(target = "songArtistOwnerName", source = "song.artistOwnerName")
    ListenResponseDTO toResponse(Listen listen, Song song);
}
