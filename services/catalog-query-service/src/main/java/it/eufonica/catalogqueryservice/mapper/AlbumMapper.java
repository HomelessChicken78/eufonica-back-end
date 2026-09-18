package it.eufonica.catalogqueryservice.mapper;

import it.eufonica.catalogqueryservice.event.album.AlbumCreatedEvent;
import it.eufonica.catalogqueryservice.model.AlbumRead;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    void updateEntityFromCreationEvent(AlbumCreatedEvent event, @MappingTarget AlbumRead existingAlbum);

    AlbumRead toEntity(AlbumCreatedEvent event);
}
