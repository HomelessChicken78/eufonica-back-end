    package it.musicplatform.catalogcommandservice.mapper;

    import it.musicplatform.catalogcommandservice.dto.song.PublishSongRequestDTO;
    import it.musicplatform.catalogcommandservice.dto.song.SongResponseDTO;
    import it.musicplatform.catalogcommandservice.model.Song;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    @Mapper(componentModel = "spring", uses = ArtistMapper.class)
    public interface SongMapper {
        @Mapping(target = "url", ignore = true)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "durationSec", ignore = true)
        @Mapping(target = "artistOwner", ignore = true)
        @Mapping(target = "amountListens", ignore = true)
        @Mapping(target = "amountLikes", ignore = true)
        @Mapping(target = "creditedArtists", ignore = true)
        Song toEntity(PublishSongRequestDTO request);

        SongResponseDTO toResponse(Song song);
    }
