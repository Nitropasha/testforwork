package org.dzhioev.ws.docservice;

import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.BatchResultStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.dzhioev.ws.docservice.service.impl.BatchDocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchSubmitServiceTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private BatchDocumentServiceImpl batchSubmitService;

    @Test
    void batchSubmit_shouldHandlePartialConflicts() {
        // given
        when(documentService.submit(eq(1L), any()))
                .thenReturn(new Document());

        when(documentService.submit(eq(2L), any()))
                .thenThrow(new ConflictException("CONFLICT", "Already submitted"));

        when(documentService.submit(eq(3L), any()))
                .thenReturn(new Document());

        var request = new BatchSubmitRequest(
                "batch-worker",
                "auto-submit",
                List.of(1L, 2L, 3L)
        );

        // when
        var response = batchSubmitService.batchSubmit(request);

        // then
        assertThat(response.results()).hasSize(3);

        assertThat(response.results().stream()
                .filter(r -> r.status() == BatchResultStatus.SUCCESS))
                .hasSize(2);

        assertThat(response.results().stream()
                .filter(r -> r.status() == BatchResultStatus.CONFLICT))
                .hasSize(1);
    }
}
