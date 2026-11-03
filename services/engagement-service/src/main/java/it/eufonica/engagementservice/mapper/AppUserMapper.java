package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.event.appuser.AppUserCreatedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser toEntity(AppUserCreatedEvent event);
}
