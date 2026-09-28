package it.eufonica.authservice.mapper;

import it.eufonica.authservice.event.artist.ArtistCreatedEvent;
import it.eufonica.authservice.model.ArtistAuthProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistAuthProjection toEntity(ArtistCreatedEvent event);
}
