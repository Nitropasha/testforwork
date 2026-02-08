package org.dzhioev.ws.docservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.BatchApproveRequest;
import org.dzhioev.ws.docservice.dto.BatchApproveResponse;
import org.dzhioev.ws.docservice.dto.BatchItemResult;
import org.dzhioev.ws.docservice.enums.BatchResultStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.exeptions.NotFoundException;
import org.dzhioev.ws.docservice.exeptions.ValidationException;
import org.dzhioev.ws.docservice.service.BatchApproveService;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BatchApproveServiceImpl implements BatchApproveService {

    private final DocumentService documentService;

    public BatchApproveServiceImpl(DocumentService documentService) {
        this.documentService = documentService;
    }

    public BatchApproveResponse batchApprove(BatchApproveRequest request) {
        validate(request);

        long start = System.currentTimeMillis();
        log.info("Batch approve started: size={}", request.documentIds().size());

        List<BatchItemResult> results = new ArrayList<>();

        for (Long documentId : request.documentIds()) {
            try {
                documentService.approve(
                        documentId,
                        new ApproveDocumentRequest(
                                request.initiator(),
                                request.comment()
                        )
                );

                results.add(success(documentId));

            } catch (NotFoundException e) {
                results.add(notFound(documentId, e));

            } catch (ConflictException e) {
                results.add(conflict(documentId, e));

            } catch (Exception e) {
                log.error("Approve failed for document {}", documentId, e);
                results.add(error(documentId, e));
            }
        }

        long time = System.currentTimeMillis() - start;
        log.info("Batch approve finished: time={}ms", time);

        return new BatchApproveResponse(results);
    }

    private void validate(BatchApproveRequest request) {
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

    private BatchItemResult success(Long id) {
        return new BatchItemResult(id, BatchResultStatus.SUCCESS, "Approved");
    }

    private BatchItemResult conflict(Long id, Exception e) {
        return new BatchItemResult(id, BatchResultStatus.CONFLICT, e.getMessage());
    }

    private BatchItemResult notFound(Long id, Exception e) {
        return new BatchItemResult(id, BatchResultStatus.NOT_FOUND, e.getMessage());
    }

    private BatchItemResult error(Long id, Exception e) {
        return new BatchItemResult(id, BatchResultStatus.ERROR, "Internal error");
    }
}
