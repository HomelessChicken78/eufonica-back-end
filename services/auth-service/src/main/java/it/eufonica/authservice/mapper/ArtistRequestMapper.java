package it.eufonica.authservice.mapper;

import it.eufonica.authservice.dto.artistrequest.ArtistRequestFullResponseDTO;
import it.eufonica.authservice.model.ArtistRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistRequestMapper {
    @Mapping(source = "requestingUser.id", target = "requestingUserId")
    @Mapping(source = "requestedArtist.id", target = "requestedArtistId")
    ArtistRequestFullResponseDTO toFullResponse(ArtistRequest request);
}
