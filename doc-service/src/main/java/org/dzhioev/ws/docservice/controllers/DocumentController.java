package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.CreateDocumentRequest;
import org.dzhioev.ws.docservice.dto.DocumentResponse;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public DocumentResponse createDoc(@RequestBody CreateDocumentRequest request) {
        Document document = documentService.createDoc(request);
        return toResponse(document);
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable Long id) {
        Document document = documentService.getDocById(id);
        return toResponse(document);
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getDocNumber(),
                document.getAuthor(),
                document.getTitle(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}

