package it.eufonica.authservice.mapper;

import it.eufonica.authservice.dto.appuser.AppUserFullResponseDTO;
import it.eufonica.authservice.dto.appuser.AppUserShortResponseDTO;
import it.eufonica.authservice.dto.auth.SignUpRequestDTO;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ArtistMapper.class)
public interface AppUserMapper {
    @Mapping(target = "affiliatedArtist", ignore = true)
    @Mapping(target = "registrationTimestamp", ignore = true)
    @Mapping(target = "id", ignore = true)
    AppUser toEntity(SignUpRequestDTO request);

    AppUserShortResponseDTO toShortResponse(AppUser appUser);

    AppUserFullResponseDTO toFullResponse(AppUser appUser);
}
