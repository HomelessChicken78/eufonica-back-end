package it.eufonica.catalogqueryservice.mapper;

import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.event.song.SongCreatedEvent;
import it.eufonica.catalogqueryservice.model.SongRead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SongMapper {
    @Mapping(target = "amountListens", ignore = true)
    @Mapping(target = "amountLikes", ignore = true)
    SongRead toEntity(SongCreatedEvent event);

    @Mapping(target = "amountListens", ignore = true)
    @Mapping(target = "amountLikes", ignore = true)
    void updateEntityFromCreationEvent(SongCreatedEvent event, @MappingTarget SongRead existingSong);

    @Mapping(target = "creditedArtistsIds", ignore = true)
    SongFullResponseDTO toEntity(SongRead songRead);
}
