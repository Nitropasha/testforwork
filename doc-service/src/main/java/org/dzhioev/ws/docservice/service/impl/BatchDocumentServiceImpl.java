package org.dzhioev.ws.docservice.service.impl;

import org.dzhioev.ws.docservice.dto.BatchItemResult;
import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.dto.BatchSubmitResponse;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.enums.BatchResultStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.exeptions.NotFoundException;
import org.dzhioev.ws.docservice.exeptions.ValidationException;
import org.dzhioev.ws.docservice.service.BatchDocumentService;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchDocumentServiceImpl implements BatchDocumentService {

    private final DocumentService documentService;

    public BatchDocumentServiceImpl(DocumentService documentService) {
        this.documentService = documentService;
    }

    public BatchSubmitResponse batchSubmit(BatchSubmitRequest request) {
        validate(request);

        List<BatchItemResult> results = new ArrayList<>();

        for (Long documentId : request.documentIds()) {
            try {
                documentService.submit(
                        documentId,
                        new SubmitDocumentRequest(
                                request.initiator(),
                                request.comment()
                        )
                );

                results.add(new BatchItemResult(
                        documentId,
                        BatchResultStatus.SUCCESS,
                        "Submitted successfully"
                ));

            } catch (NotFoundException e) {
                results.add(new BatchItemResult(
                        documentId,
                        BatchResultStatus.NOT_FOUND,
                        e.getMessage()
                ));

            } catch (ConflictException e) {
                results.add(new BatchItemResult(
                        documentId,
                        BatchResultStatus.CONFLICT,
                        e.getMessage()
                ));
            }
        }

        return new BatchSubmitResponse(results);
    }

    private void validate(BatchSubmitRequest request) {
        if (request.initiator() == null || request.initiator().isBlank()) {
            throw new ValidationException("INITIATOR_EMPTY", "Initiator must not be empty");
        }
        if (request.documentIds() == null || request.documentIds().isEmpty()) {
            throw new ValidationException("IDS_EMPTY", "Document ids must not be empty");
        }
        if (request.documentIds().size() > 1000) {
            throw new ValidationException("IDS_LIMIT", "Maximum 1000 document ids allowed");
        }
    }
}
