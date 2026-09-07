package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AudioStorageServiceImpl implements AudioStorageService {
    @Override
    public String store(MultipartFile file) {
        // TODO send to S3

        return "photos/2026/09/vacation.jpg";
    }
}
