package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/songs/{songId}")
@RequiredArgsConstructor @Slf4j
public class SongEngagementController {
    private final LikeService likeService;

    @PutMapping(path = "/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(@PathVariable UUID songId) {
        likeService.likeSong(songId);
        log.debug("Liking song with id={}.", songId);
    }

    @DeleteMapping(path = "/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable UUID songId) {
        likeService.unlikeSong(songId);
        log.debug("Unliking song with id={}.", songId);
    }
}
