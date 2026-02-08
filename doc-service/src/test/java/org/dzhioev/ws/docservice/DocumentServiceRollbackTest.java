package org.dzhioev.ws.docservice;

import org.dzhioev.ws.docservice.dao.ApprovalRegistryRepository;
import org.dzhioev.ws.docservice.dao.DocumentRepository;
import org.dzhioev.ws.docservice.dao.DocumentStatusHistoryRepository;
import org.dzhioev.ws.docservice.dto.ApproveDocumentRequest;
import org.dzhioev.ws.docservice.dto.SubmitDocumentRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.DocumentStatus;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class DocumentServiceRollbackTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentStatusHistoryRepository historyRepository;

    @MockitoBean
    private ApprovalRegistryRepository approvalRegistryRepository;

    @Test
    void approve_shouldRollback_whenRegistrySaveFails() {
        // given
        Document doc = new Document();
        doc.setDocNumber("DOC-1");
        doc.setAuthor("Author");
        doc.setTitle("Title");
        doc.setStatus(DocumentStatus.DRAFT);

        doc = documentRepository.save(doc);

        documentService.submit(
                doc.getId(),
                new SubmitDocumentRequest("user", "submit")
        );

        long historyBeforeApprove = historyRepository.count();

        Mockito.when(approvalRegistryRepository.save(Mockito.any()))
                .thenThrow(new RuntimeException("DB error"));

        // when
        Document finalDoc = doc;
        assertThatThrownBy(() ->
                documentService.approve(
                        finalDoc.getId(),
                        new ApproveDocumentRequest("user", "approve")
                )
        ).isInstanceOf(RuntimeException.class);

        // then
        Document reloaded = documentRepository.findById(doc.getId()).orElseThrow();

        assertThat(reloaded.getStatus())
                .as("Статус должен остаться SUBMITTED")
                .isEqualTo(DocumentStatus.SUBMITTED);

        assertThat(historyRepository.count())
                .as("История approve не должна сохраниться")
                .isEqualTo(historyBeforeApprove);
    }
}
