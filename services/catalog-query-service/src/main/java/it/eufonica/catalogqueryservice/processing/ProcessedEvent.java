package it.eufonica.catalogqueryservice.processing;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime processedAt = LocalDateTime.now();
}
