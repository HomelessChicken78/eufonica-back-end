package it.eufonica.catalogcommandservice.mapper;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.event.album.AlbumCreatedEvent;
import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "pubDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    Album toEntity(AlbumCreationRequestDTO request);

    @Mapping(target = "numberOfSongs", ignore = true)
    AlbumSummaryResponseDTO toSummaryResponse(Album entity);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "pubDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    void updateEntityFromDto(AlbumCreationRequestDTO request, @MappingTarget Album album);

    @AfterMapping
    default void mapNumberOfSongs(Album entity, @MappingTarget AlbumSummaryResponseDTO dto) {
        dto.setNumberOfSongs(entity.getSongs().size());
    }

    AlbumCreatedEvent toCreatedEvent(Album album);

    AlbumCreatedEvent.Artist toAlbumArtist(Artist artist);

    AlbumCreatedEvent.Song toAlbumSong(Song song);
}
