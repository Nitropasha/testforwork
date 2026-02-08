package org.dzhioev.ws.docservice.service.impl;

import org.dzhioev.ws.docservice.dao.ApprovalRegistryRepository;
import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dao.DocumentStatusHistoryRepository;
import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.CreateDocumentRequest;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.entity.ApprovalRegistry;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.entity.DocumentStatusHistory;
import org.dzhioev.ws.docservice.enums.DocumentAction;
import org.dzhioev.ws.docservice.enums.DocumentStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.exeptions.NotFoundException;
import org.dzhioev.ws.docservice.exeptions.ValidationException;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentStatusHistoryRepository historyRepository;
    private final ApprovalRegistryRepository approvalRegistryRepository;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            DocumentStatusHistoryRepository historyRepository,
            ApprovalRegistryRepository approvalRegistryRepository
    ) {
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.approvalRegistryRepository = approvalRegistryRepository;
    }

    public Document createDoc(CreateDocumentRequest request) {
        validateCreateRequest(request);

        Document document = new Document();
        document.setDocNumber(generateDocNumber());
        document.setAuthor(request.author());
        document.setTitle(request.title());
        document.setStatus(DocumentStatus.DRAFT);

        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public Document getDocById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "DOCUMENT_NOT_FOUND",
                        "Document with id=" + id + " not found"
                ));
    }


    public Document submit(Long id, SubmitDocumentRequest request) {
        validateSubmitRequest(request);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "DOCUMENT_NOT_FOUND",
                        "Document with id=" + id + " not found"
                ));

        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new ConflictException(
                    "INVALID_STATUS",
                    "Document must be in DRAFT status to submit"
            );
        }

        DocumentStatus from = document.getStatus();
        document.setStatus(DocumentStatus.SUBMITTED);

        DocumentStatusHistory history = new DocumentStatusHistory();
        history.setDocument(document);
        history.setAction(DocumentAction.SUBMIT);
        history.setFromStatus(from);
        history.setToStatus(DocumentStatus.SUBMITTED);
        history.setPerformedBy(request.initiator());
        history.setComment(request.comment());

        documentRepository.save(document);
        historyRepository.save(history);

        return document;
    }

    public Document approve(Long id, ApproveDocumentRequest request) {
        validateApproveRequest(request);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "DOCUMENT_NOT_FOUND",
                        "Document with id=" + id + " not found"
                ));

        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            throw new ConflictException(
                    "INVALID_STATUS",
                    "Document must be in SUBMITTED status to approve"
            );
        }

        DocumentStatus from = document.getStatus();


        // to Registry
        ApprovalRegistry registry = new ApprovalRegistry();
        registry.setDocument(document);
        registry.setApprovedBy(request.initiator());
        approvalRegistryRepository.save(registry);

        // to History
        DocumentStatusHistory history = new DocumentStatusHistory();
        history.setDocument(document);
        history.setAction(DocumentAction.APPROVE);
        history.setFromStatus(from);
        history.setToStatus(DocumentStatus.APPROVED);
        history.setPerformedBy(request.initiator());
        history.setComment(request.comment());
        historyRepository.save(history);

        // to Document
        // change status at the end
        document.setStatus(DocumentStatus.APPROVED);
        documentRepository.save(document);

        return document;
    }

    private void validateApproveRequest(ApproveDocumentRequest request) {
        if (request.initiator() == null || request.initiator().isBlank()) {
            throw new ValidationException(
                    "INITIATOR_EMPTY",
                    "Initiator must not be empty"
            );
        }
    }

    private void validateSubmitRequest(SubmitDocumentRequest request) {
        if (request.initiator() == null || request.initiator().isBlank()) {
            throw new ValidationException(
                    "INITIATOR_EMPTY",
                    "Initiator must not be empty"
            );
        }
    }
    private void validateCreateRequest(CreateDocumentRequest request) {
        if (request.author() == null || request.author().isBlank()) {
            throw new ValidationException("AUTHOR_EMPTY", "Author must not be empty");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new ValidationException("TITLE_EMPTY", "Title must not be empty");
        }
    }

    private String generateDocNumber() {
        return "DOC-" + UUID.randomUUID();
    }
}
