package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "amountLikes", constant = "0")
    Playlist toEntity(PlaylistCreateRequestDTO request);

    @Mapping(target = "createdPlaylistId", source = "id")
    PlaylistCreatedResponseDTO toCreatedResponse(Playlist entity);
}
