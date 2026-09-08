package it.eufonica.catalogcommandservice.mapper;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.model.Album;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    @Mapping(target = "pubDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    Album toEntity(AlbumCreationRequestDTO request);

    @Mapping(target = "numberOfSongs", ignore = true)
    AlbumSummaryResponseDTO toSummaryResponse(Album entity);

    @AfterMapping
    default void mapNumberOfSongs(Album entity, @MappingTarget AlbumSummaryResponseDTO dto) {
        dto.setNumberOfSongs(entity.getSongs().size());
    }
}
