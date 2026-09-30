package it.eufonica.authservice.mapper;

import it.eufonica.authservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.authservice.dto.artistrequest.ArtistRequestFullResponseDTO;
import it.eufonica.authservice.dto.artistrequest.ArtistRequestShortResponseDTO;
import it.eufonica.authservice.dto.artistrequest.SendExistingArtistRequestDTO;
import it.eufonica.authservice.dto.artistrequest.SendNewArtistRequestDTO;
import it.eufonica.authservice.model.ArtistRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistRequestMapper {
    @Mapping(source = "requestingUser.id", target = "requestingUserId")
    @Mapping(source = "requestedArtist.id", target = "requestedArtistId")
    ArtistRequestFullResponseDTO toFullResponse(ArtistRequest entity);

    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "status", expression = "java(ArtistRequest.RequestStatus.PENDING)")
    @Mapping(target = "requestingUser", ignore = true)
    @Mapping(target = "requestedArtist", ignore = true)
    @Mapping(target = "id", ignore = true)
    ArtistRequest toEntity(SendNewArtistRequestDTO dto);

    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "status", expression = "java(ArtistRequest.RequestStatus.PENDING)")
    @Mapping(target = "requestingUser", ignore = true)
    @Mapping(target = "requestedName", ignore = true)
    @Mapping(target = "requestedFoundationDate", ignore = true)
    @Mapping(target = "requestedArtist", ignore = true)
    @Mapping(target = "id", ignore = true)
    ArtistRequest toEntity(SendExistingArtistRequestDTO request);

    @Mapping(source = "requestingUser.id", target = "requestingUserId")
    @Mapping(source = "requestedArtist.id", target = "requestedArtistId")
    ArtistRequestShortResponseDTO toShortResponse(ArtistRequest entity);

    @Mapping(target = "name", source = "requestedName")
    @Mapping(target = "foundationDate", source = "requestedFoundationDate")
    ArtistCreationRequestDTO toArtistCreationRequest(ArtistRequest artistRequest);
}
