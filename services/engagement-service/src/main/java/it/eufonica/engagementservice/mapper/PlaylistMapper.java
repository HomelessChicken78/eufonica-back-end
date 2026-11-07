package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryResponseDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistResponseDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.web.dto.PageResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "numberOfEntries", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "amountLikes", constant = "0")
    Playlist toEntity(PlaylistCreateRequestDTO request);

    @Mapping(target = "createdPlaylistId", source = "id")
    PlaylistCreatedResponseDTO toCreatedResponse(Playlist entity);

    @Mapping(target = "creatorId", source = "creator.id")
    PlaylistShortResponseDTO toShortResponse(Playlist entity);

    @Mapping(target = "creatorId", source = "entity.creator.id")
    @Mapping(target = "playlistEntries", source = "pagedEntries")
    PlaylistResponseDTO toFullResponse(Playlist entity, PageResponseDTO<PlaylistEntryResponseDTO> pagedEntries);
}
