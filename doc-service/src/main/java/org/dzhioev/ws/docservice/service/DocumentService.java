package org.dzhioev.ws.docservice.service;

import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.CreateDocumentRequest;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.entity.Document;

public interface DocumentService {

    Document createDoc(CreateDocumentRequest request);

    Document getDocById(Long id);

    Document submit(Long id, SubmitDocumentRequest request);

    Document approve(Long id, ApproveDocumentRequest request);
}