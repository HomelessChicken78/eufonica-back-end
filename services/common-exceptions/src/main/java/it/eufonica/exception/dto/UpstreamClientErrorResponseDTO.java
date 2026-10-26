package it.eufonica.exception.dto;

import it.eufonica.exception.CommonErrorReason;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Getter @Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpstreamClientErrorResponseDTO {
    private String message;

    @EqualsAndHashCode.Exclude
    private LocalDateTime timestamp;

    private int status = 400;

    private String reason;

    public enum Field {
        MESSAGE, TIMESTAMP, STATUS, REASON
    }

    /**
     * Normalize null {@code message} and null {@code reason} fields by setting them to
     * {@code "unknown error"} and {@code CommonErrorReason.UNEXPECTED_ERROR.toString()} respectively.
     *
     * @return a set containing all the fields that have been normalized.
     */
    public EnumSet<Field> normalizeDefaults() {
        EnumSet<Field> normalized = EnumSet.noneOf(Field.class);
        if (message == null || message.isEmpty()) {
            message = "unknown error.";
            normalized.add(Field.MESSAGE);
        }
        if (reason == null) {
            reason = CommonErrorReason.UNEXPECTED_ERROR.toString();
            normalized.add(Field.REASON);
        }
        return normalized;
    }
}
