package it.eufonica.catalogqueryservice.mapper;

import it.eufonica.catalogqueryservice.dto.artist.ArtistFullResponseDTO;
import it.eufonica.catalogqueryservice.event.artist.ArtistCreatedEvent;
import it.eufonica.catalogqueryservice.event.artist.ArtistUpdatedEvent;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    void updateEntityFromCreationEvent(ArtistCreatedEvent event, @MappingTarget ArtistRead existingAlbum);

    ArtistRead toEntity(ArtistCreatedEvent event);

    @Mapping(target = "registrationDate", ignore = true)
    ArtistRead toEntity(ArtistUpdatedEvent event);

    ArtistFullResponseDTO toFullResponse(ArtistRead found);
}
