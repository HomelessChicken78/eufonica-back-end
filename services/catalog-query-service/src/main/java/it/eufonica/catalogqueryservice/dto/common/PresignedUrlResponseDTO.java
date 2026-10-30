package it.eufonica.catalogqueryservice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class PresignedUrlResponseDTO {
    private String url;
    private Instant expiresAt;
}
