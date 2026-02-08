package org.dzhioev.ws.docservice;

import org.dzhioev.ws.docservice.dao.ApprovalRegistryRepository;
import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dao.DocumentStatusHistoryRepository;
import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.CreateDocumentRequest;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.entity.DocumentStatusHistory;
import org.dzhioev.ws.docservice.enums.DocumentAction;
import org.dzhioev.ws.docservice.enums.DocumentStatus;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class DocumentHappyPathTest {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentStatusHistoryRepository historyRepository;

    @Autowired
    ApprovalRegistryRepository approvalRegistryRepository;

    @Test
    void happyPath_singleDocument() {

        Document created = documentService.createDoc(
                new CreateDocumentRequest(
                        "Author-1",
                        "Test document"
                )
        );

        assertThat(created.getStatus())
                .isEqualTo(DocumentStatus.DRAFT);

        Long docId = created.getId();


        Document submitted = documentService.submit(
                docId,
                new SubmitDocumentRequest(
                        "user1",
                        "submit comment"
                )
        );

        assertThat(submitted.getStatus())
                .isEqualTo(DocumentStatus.SUBMITTED);

        Document approved = documentService.approve(
                docId,
                new ApproveDocumentRequest(
                        "user2",
                        "approve comment"
                )
        );

        assertThat(approved.getStatus())
                .isEqualTo(DocumentStatus.APPROVED);

        Document reloaded = documentRepository.findById(docId).orElseThrow();
        assertThat(reloaded.getStatus())
                .isEqualTo(DocumentStatus.APPROVED);

        var history = historyRepository.findAll().stream()
                .filter(h -> h.getDocument().getId().equals(docId))
                .toList();

        assertThat(history)
                .extracting(DocumentStatusHistory::getAction)
                .containsExactlyInAnyOrder(
                        DocumentAction.SUBMIT,
                        DocumentAction.APPROVE
                );

        assertThat(
                approvalRegistryRepository.findByDocumentId(docId)
        ).isPresent();
    }
}
