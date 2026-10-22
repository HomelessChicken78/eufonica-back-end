package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Set<Album> findBySongsContaining(Song toDelete);

    @Query(
            """
            SELECT art FROM Artist AS art JOIN art.creditedSongs AS s
            WHERE s.id IN :songIds
            AND art.id IN :artistIds
            
            GROUP BY art.id
            HAVING COUNT(DISTINCT(s.id)) = :songCount
            """
    )
    List<Artist> findCommonCreditedArtists(@Param("artistIds") Set<UUID> artistIds, @Param("songIds") Set<UUID> songIds, @Param("songCount") int songCount);

    @Query(
            """
            SELECT art FROM Album AS al
                JOIN al.artists AS art
                JOIN art.creditedSongs AS s
            
            WHERE al.id = :albumId
            AND s MEMBER OF al.songs
            
            GROUP BY art.id
            HAVING COUNT(DISTINCT(s.id)) = SIZE(al.songs)
            """
    )
    List<Artist> findCommonCreditedArtists(@Param("albumId") UUID albumId);
}
