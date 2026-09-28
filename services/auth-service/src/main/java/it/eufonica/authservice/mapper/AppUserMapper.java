package it.eufonica.authservice.mapper;

import it.eufonica.authservice.dto.SignUpRequestDTO;
import it.eufonica.authservice.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    @Mapping(target = "registrationTimestamp", ignore = true)
    @Mapping(target = "id", ignore = true)
    AppUser toEntity(SignUpRequestDTO request);
}
