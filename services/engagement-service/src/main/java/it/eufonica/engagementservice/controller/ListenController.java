package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.dto.listen.ListenResponseDTO;
import it.eufonica.engagementservice.service.ListenService;
import it.eufonica.web.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/listens")
@RequiredArgsConstructor @Slf4j
public class ListenController {
    private final ListenService listenService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<ListenResponseDTO> getHistory(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${LISTEN_PAGE_DEFAULT_SIZE:50}") Integer pageSize
    ) {
        PageResponseDTO<ListenResponseDTO> response = listenService.getListenHistory(pageNumber, pageSize);

        log.debug("Searched listen history. (page {}/{} - {} results)",
                response.getCurrentPage(), response.getTotalPages(), response.getContent().size());

        return response;
    }
}
