package it.eufonica.catalogqueryservice.processing;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcessedEvent {
    @EqualsAndHashCode.Include
    @Id private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime processedAt = LocalDateTime.now();
}
