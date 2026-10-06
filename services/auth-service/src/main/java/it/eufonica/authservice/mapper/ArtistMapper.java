package it.eufonica.authservice.mapper;

import it.eufonica.authservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.authservice.event.artist.ArtistCreatedEvent;
import it.eufonica.authservice.model.ArtistAuthProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistAuthProjection toEntity(ArtistCreatedEvent event);

    ArtistAuthProjection toEntity(ArtistSummaryResponseDTO artistCreatedResponse);

    @Mapping(target = "foundationDate", ignore = true)
    ArtistSummaryResponseDTO toSummaryResponse(ArtistAuthProjection artistCreatedResponse);
}
