package it.eufonica.catalogcommandservice.dto.song;

import lombok.*;
import org.apache.tika.mime.MimeType;

@AllArgsConstructor @NoArgsConstructor
@Data
public class AudioMetadataDTO {
    private MimeType mimeType;
    private String extension;
}
