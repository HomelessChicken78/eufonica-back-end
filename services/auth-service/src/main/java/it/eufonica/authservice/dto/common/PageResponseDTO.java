package it.eufonica.authservice.dto.common;

import lombok.*;

import java.util.List;

@AllArgsConstructor @NoArgsConstructor @Builder
@Getter @Setter
public class PageResponseDTO<T> {
    private List<T> content;
    private Number currentPage;
    private Number totalPages;
    private Number totalElements;
    private Number pageSize;
}

