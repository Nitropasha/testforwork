package org.dzhioev.ws.docservice.service.impl;

import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dto.DocumentSearchFilter;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.service.DocumentSearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final DocumentRepository documentRepository;

    public DocumentSearchServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<Document> search(DocumentSearchFilter filter, int limit) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        return documentRepository
                .findAll(DocumentSpecifications.byFilter(filter), pageable)
                .getContent();
    }
}
