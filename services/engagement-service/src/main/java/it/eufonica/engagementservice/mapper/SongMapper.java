package it.eufonica.engagementservice.mapper;

import it.eufonica.engagementservice.model.Song;
import it.eufonica.event.song.SongCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SongMapper {

    @Mapping(target = "artistOwnerName", expression = "java(getSongOwnerName(event))")
    Song toEntity(SongCreatedEvent event);

    @Mapping(target = "artistOwnerName", expression = "java(getSongOwnerName(event))")
    void updateEntityFromCreationEvent(SongCreatedEvent event, @MappingTarget Song existingSong);


    /**
     * Cycle through event.creditedArtists to find the name associated to event.artistOwnerId
     *
     * @param event the event to search the owner's name of
     *
     * @return The name of the song's owner
     */
    default String getSongOwnerName(SongCreatedEvent event) {
        for (SongCreatedEvent.ArtistCredited art : event.getCreditedArtists())
            if (event.getArtistOwnerId().equals(art.getId()))
                return art.getName();

        return null;
    }
}
