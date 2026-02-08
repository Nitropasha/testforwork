package org.dzhioev.ws.docservice.service;

import org.dzhioev.ws.docservice.dto.DocumentSearchFilter;
import org.dzhioev.ws.docservice.entity.Document;

import java.util.List;

public interface DocumentSearchService {
    List<Document> search(DocumentSearchFilter filter, int limit);
}