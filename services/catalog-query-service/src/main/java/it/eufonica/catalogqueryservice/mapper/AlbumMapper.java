package it.eufonica.catalogqueryservice.mapper;

import it.eufonica.catalogqueryservice.dto.album.AlbumFullResponseDTO;
import it.eufonica.catalogqueryservice.event.album.AlbumCreatedEvent;
import it.eufonica.catalogqueryservice.event.album.AlbumUpdatedEvent;
import it.eufonica.catalogqueryservice.model.AlbumRead;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    void updateEntityFromCreationEvent(AlbumCreatedEvent event, @MappingTarget AlbumRead existingAlbum);

    AlbumRead toEntity(AlbumCreatedEvent event);

    @Mapping(target = "pubDate", ignore = true)
    AlbumRead toEntity(AlbumUpdatedEvent event);

    @Mapping(target = "songIds", ignore = true)
    @Mapping(target = "artistIds", ignore = true)
    AlbumFullResponseDTO toFullResponse(AlbumRead found);
}
