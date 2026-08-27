package it.musicplatform.catalogcommandservice.mapper;

import it.musicplatform.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.musicplatform.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.musicplatform.catalogcommandservice.model.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "ownedSongs", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditedSongs", ignore = true)
    Artist toEntity(ArtistCreationRequestDTO creationRequestDTO);

    ArtistSummaryResponseDTO toSummaryResponse(Artist savedArtist);
}
