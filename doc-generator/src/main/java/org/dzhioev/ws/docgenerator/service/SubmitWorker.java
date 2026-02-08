package org.dzhioev.ws.docgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.dzhioev.ws.docgenerator.dto.BatchSubmitRequest;
import org.dzhioev.ws.docgenerator.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(
        name = "worker.submit.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class SubmitWorker {

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final int batchSize;

    public SubmitWorker(
            RestTemplate restTemplate,
            @Value("${generator.service-url}") String serviceUrl,
            @Value("${generator.batchSize}") int batchSize
    ) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${worker.submit.delay-ms}")
    public void run() {
        System.out.println("SUBMIT-worker started");
        long start = System.currentTimeMillis();

        ResponseEntity<DocumentDto[]> response =
                restTemplate.getForEntity(
                        serviceUrl + "/documents/search?status=DRAFT&limit=" + batchSize,
                        DocumentDto[].class
                );

        DocumentDto[] documents = response.getBody();
        if (documents == null || documents.length == 0) {
            return;
        }

        List<Long> ids = Arrays.stream(documents)
                .map(DocumentDto::id)
                .toList();

        log.info("SUBMIT-worker found {} documents", ids.size());

        restTemplate.postForEntity(
                serviceUrl + "/documents/batch/submit",
                new BatchSubmitRequest(
                        "SUBMIT-worker",
                        "auto-submit",
                        ids
                ),
                Void.class
        );

        log.info(
                "SUBMIT-worker finished in {}ms",
                System.currentTimeMillis() - start
        );
    }
}
