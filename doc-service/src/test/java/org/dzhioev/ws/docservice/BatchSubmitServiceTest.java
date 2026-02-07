package org.dzhioev.ws.docservice;

import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.enums.BatchResultStatus;
import org.dzhioev.ws.docservice.exeptions.ConflictException;
import org.dzhioev.ws.docservice.service.BatchDocumentService;
import org.dzhioev.ws.docservice.service.DocumentService;
import org.dzhioev.ws.docservice.service.impl.BatchDocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BatchSubmitServiceTest {

    @TestConfiguration
    static class TestConfig {

        @Primary
        @Bean
        public DocumentService documentService() {
            return Mockito.mock(DocumentService.class);
        }

        @Primary
        @Bean
        public BatchDocumentService batchSubmitService(DocumentService documentService) {
            return new BatchDocumentServiceImpl(documentService);
        }
    }

    @Autowired
    private BatchDocumentService batchSubmitService;

    @Autowired
    private DocumentService documentService;

    @Test
    void batchSubmit_shouldHandlePartialConflicts() {
        // твой тестовый код как раньше
    }
}