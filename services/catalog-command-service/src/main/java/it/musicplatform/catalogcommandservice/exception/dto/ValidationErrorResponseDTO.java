package it.musicplatform.catalogcommandservice.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponseDTO {
    private Map<String, String> errors;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Integer status = 400;

    public ValidationErrorResponseDTO(Map<String, String> errors) {
        this.errors = errors;
    }

    public ValidationErrorResponseDTO(List<String> fields, List<String> messages) {
        if (fields.size() != messages.size())
            throw new IllegalArgumentException("Number of fields must match number of messages"); // Inglese perché è un errore lato programmazione, non lato API/applicazione

        this.errors = new LinkedHashMap<>();

        for (int i = 0; i < fields.size(); i++)
            errors.put(fields.get(i), messages.get(i));
    }

    public ValidationErrorResponseDTO(Map<String, String> errors, Integer status) {
        this.errors = errors;
        this.status = status;
    }
}