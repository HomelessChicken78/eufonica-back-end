package it.eufonica.catalogcommandservice.repository;

import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Set<Album> findBySongsContaining(Song toDelete);

    @Query(
            """
            SELECT EXISTS (
            	SELECT art.id FROM Artist AS art JOIN art.creditedSongs AS s
            	WHERE s.id IN :songIds
            	AND art.id IN :artistIds
            
            	GROUP BY art.id
            	HAVING COUNT(DISTINCT(s.id)) = :songCount
            )
            """
    )
    boolean existsCommonCreditedArtist(Set<UUID> artistIds, Set<UUID> songIds, int songCount);
}
