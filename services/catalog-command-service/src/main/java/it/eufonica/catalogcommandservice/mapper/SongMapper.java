    package it.eufonica.catalogcommandservice.mapper;

    import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
    import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
    import it.eufonica.catalogcommandservice.event.song.SongCreatedEvent;
    import it.eufonica.catalogcommandservice.model.Artist;
    import it.eufonica.catalogcommandservice.model.Song;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    @Mapper(componentModel = "spring", uses = ArtistMapper.class)
    public interface SongMapper {
        @Mapping(target = "version", ignore = true)
        @Mapping(target = "audio", ignore = true)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "durationSec", ignore = true)
        @Mapping(target = "artistOwner", ignore = true)
        @Mapping(target = "amountListens", ignore = true)
        @Mapping(target = "amountLikes", ignore = true)
        @Mapping(target = "creditedArtists", ignore = true)
        Song toEntity(PublishSongRequestDTO request);

        SongResponseDTO toResponse(Song song);

        @Mapping(target = "artistOwnerId", source = "artistOwner.id")
        SongCreatedEvent toCreatedEvent(Song song);

        SongCreatedEvent.ArtistCredited toArtistCredited(Artist artist);
    }
