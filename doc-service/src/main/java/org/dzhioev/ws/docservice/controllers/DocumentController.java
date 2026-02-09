package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.CreateDocumentRequest;
import org.dzhioev.ws.docservice.dto.DocumentResponse;
import org.dzhioev.ws.docservice.dto.DocumentSearchFilter;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.DocumentStatus;
import org.dzhioev.ws.docservice.mapper.DocumentMapper;
import org.dzhioev.ws.docservice.service.DocumentSearchService;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentSearchService searchService;
    private final DocumentMapper documentMapper;

    public DocumentController(DocumentService documentService, DocumentSearchService searchService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.searchService = searchService;
        this.documentMapper = documentMapper;
    }

    @PostMapping
    public DocumentResponse createDoc(@RequestBody CreateDocumentRequest request) {
        Document document = documentService.createDoc(request);
        return documentMapper.toResponse(document);
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable Long id) {
        Document document = documentService.getDocById(id);
        return documentMapper.toResponse(document);
    }

    @PostMapping("/{id}/submit")
    public DocumentResponse submit(
            @PathVariable Long id,
            @RequestBody SubmitDocumentRequest request
    ) {
        Document document = documentService.submit(id, request);
        return documentMapper.toResponse(document);
    }

    @PostMapping("/{id}/approve")
    public DocumentResponse approve(
            @PathVariable Long id,
            @RequestBody ApproveDocumentRequest request
    ) {
        Document document = documentService.approve(id, request);
        return documentMapper.toResponse(document);
    }

    @GetMapping("/search")
    public List<DocumentResponse> search(
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo,
            @RequestParam(required = false, defaultValue = "100") int limit
    ) {
        DocumentSearchFilter filter = new DocumentSearchFilter(
                status, author, dateFrom, dateTo
        );

        return searchService.search(filter, limit)
                .stream()
                .map(documentMapper::toResponse)
                .toList();
    }

}

