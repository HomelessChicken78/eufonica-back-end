package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.model.PlaylistEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistEntryMapper {
    @Mapping(target = "createdEntryId", source = "id")
    PlaylistEntryShortResponseDTO toShortResponse(PlaylistEntry entity);
}
