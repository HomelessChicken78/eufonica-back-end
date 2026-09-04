package it.eufonica.catalogcommandservice.mapper;

import it.eufonica.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.catalogcommandservice.model.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "ownedSongs", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditedSongs", ignore = true)
    Artist toEntity(ArtistCreationRequestDTO creationRequestDTO);

    ArtistSummaryResponseDTO toSummaryResponse(Artist savedArtist);

    Set<String> toName(Set<Artist> artists);

    default String toName(Artist artist) {
        if (artist == null) return null;
        return artist.getName();
    }
}
