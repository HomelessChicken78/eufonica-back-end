package it.eufonica.event.appuser;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor @AllArgsConstructor
public class AppUserCreatedEvent {
    private UUID id;
    private String sub;
}
