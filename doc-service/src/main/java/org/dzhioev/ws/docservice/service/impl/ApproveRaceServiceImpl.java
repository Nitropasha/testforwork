package org.dzhioev.ws.docservice.service.impl;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.ApproveRaceRequest;
import org.dzhioev.ws.docservice.dto.ApproveRaceResponse;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.DocumentStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.exeptions.ValidationException;
import org.dzhioev.ws.docservice.service.ApproveRaceService;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ApproveRaceServiceImpl implements ApproveRaceService {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    public ApproveRaceServiceImpl(
            DocumentService documentService,
            DocumentRepository documentRepository
    ) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    public ApproveRaceResponse runRace(Long documentId, ApproveRaceRequest request)
            throws InterruptedException {

        validate(request);

        ExecutorService executor = Executors.newFixedThreadPool(request.threads());

        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        AtomicInteger error = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(request.attempts());

        for (int i = 0; i < request.attempts(); i++) {
            executor.submit(() -> {
                try {
                    documentService.approve(
                            documentId,
                            new ApproveDocumentRequest(
                                    request.initiator(),
                                    "race-approve"
                            )
                    );
                    success.incrementAndGet();

                } catch (ConflictException | OptimisticLockException e) {
                    conflict.incrementAndGet();

                } catch (Exception e) {
                    error.incrementAndGet();
                    log.error("Approve failed for document {}: {}", documentId, e.toString(), e);

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        DocumentStatus finalStatus = documentRepository.findById(documentId)
                .map(Document::getStatus)
                .orElse(null);

        return new ApproveRaceResponse(
                success.get(),
                conflict.get(),
                error.get(),
                finalStatus
        );
    }

    private void validate(ApproveRaceRequest request) {
        if (request.threads() <= 0 || request.attempts() <= 0) {
            throw new ValidationException(
                    "INVALID_PARAMS",
                    "threads and attempts must be > 0"
            );
        }
        if (request.threads() > request.attempts()) {
            throw new ValidationException(
                    "INVALID_PARAMS",
                    "threads must be <= attempts"
            );
        }
        if (request.initiator() == null || request.initiator().isBlank()) {
            throw new ValidationException(
                    "INITIATOR_EMPTY",
                    "Initiator must not be empty"
            );
        }
    }
}
