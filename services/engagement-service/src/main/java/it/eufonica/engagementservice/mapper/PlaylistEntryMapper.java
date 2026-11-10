package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryResponseDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PlaylistEntryMapper {
    @Mapping(target = "createdEntryId", source = "id")
    PlaylistEntryShortResponseDTO toShortResponse(PlaylistEntry entity);

    @Mapping(target = "rankOrder", ignore = true)
    @Mapping(target = "id", source = "entity.id")
    PlaylistEntryResponseDTO toFullResponse(PlaylistEntry entity, Song song);

    default List<PlaylistEntryResponseDTO> toFullResponsesWithOrder(
            List<PlaylistEntry> entities, Map<UUID, Song> entrySongs, Integer pageOffset
    ) {
        List<PlaylistEntryResponseDTO> mappedResult = new ArrayList<>();
        int index = 1;

        for (PlaylistEntry entry : entities) {
            PlaylistEntryResponseDTO mappedEntry = toFullResponse(entry, entrySongs.get(entry.getSongId()));
            mappedEntry.setRankOrder(pageOffset + index);
            mappedResult.add(mappedEntry);
            index++;
        }

        return mappedResult;
    }
}
