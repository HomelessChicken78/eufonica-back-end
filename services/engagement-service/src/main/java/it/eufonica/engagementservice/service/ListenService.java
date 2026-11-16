package it.eufonica.engagementservice.service;

import java.util.UUID;

public interface ListenService {
    void listen(UUID songId, int secPlayed);
}
