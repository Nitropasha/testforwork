package org.dzhioev.ws.docservice;

import org.dzhioev.ws.docservice.dto.BatchApproveRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.BatchResultStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.dzhioev.ws.docservice.service.impl.BatchApproveServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BatchApproveServiceTest {

    @Mock
    DocumentService documentService;

    @InjectMocks
    BatchApproveServiceImpl batchApproveService;

    @Test
    void batchApprove_shouldReturnAllSuccess() {
        when(documentService.approve(anyLong(), any()))
                .thenReturn(new Document());

        var request = new BatchApproveRequest(
                "tester",
                "comment",
                List.of(1L, 2L, 3L)
        );

        var response = batchApproveService.batchApprove(request);

        assertThat(response.results())
                .allMatch(r -> r.status() == BatchResultStatus.SUCCESS);
    }

    @Test
    void batchApprove_shouldHandlePartialConflict() {
        when(documentService.approve(eq(1L), any()))
                .thenReturn(new Document());
        when(documentService.approve(eq(2L), any()))
                .thenThrow(new ConflictException("CONFLICT", "Already approved"));
        when(documentService.approve(eq(3L), any()))
                .thenReturn(new Document());

        var request = new BatchApproveRequest(
                "tester",
                "comment",
                List.of(1L, 2L, 3L)
        );

        var response = batchApproveService.batchApprove(request);

        assertThat(response.results().stream()
                .filter(r -> r.status() == BatchResultStatus.SUCCESS))
                .hasSize(2);

        assertThat(response.results().stream()
                .filter(r -> r.status() == BatchResultStatus.CONFLICT))
                .hasSize(1);
    }
}
