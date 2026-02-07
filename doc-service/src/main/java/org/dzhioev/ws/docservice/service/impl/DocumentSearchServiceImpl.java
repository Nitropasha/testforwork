package org.dzhioev.ws.docservice.service.impl;

import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dto.DocumentSearchFilter;
import org.dzhioev.ws.docservice.entity.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {

    private final DocumentRepository documentRepository;

    public DocumentSearchService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> search(DocumentSearchFilter filter) {
        return documentRepository.findAll(
                DocumentSpecifications.byFilter(filter)
        );
    }
}