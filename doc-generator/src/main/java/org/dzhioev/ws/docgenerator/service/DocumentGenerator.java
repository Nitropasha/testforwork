package org.dzhioev.ws.docgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
@ConditionalOnProperty(
        name = "generator.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DocumentGenerator implements CommandLineRunner {

    @Value("${generator.count}")
    private int count;

    @Value("${generator.batchSize}")
    private int batchSize;

    @Value("${generator.service-url}")
    private String serviceUrl;

    private final RestTemplate restTemplate;

    public DocumentGenerator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws InterruptedException {
        log.info("Document generation started: total={}, batchSize={}", count, batchSize);

        long start = System.currentTimeMillis();
        int created = 0;

        while (created < count) {
            int batch = Math.min(batchSize, count - created);

            for (int i = 0; i < batch; i++) {
                try {
                    restTemplate.postForEntity(
                            serviceUrl + "/documents",
                            Map.of(
                                    "author", "Author-" + (created + i + 1),
                                    "title", "Document-" + (created + i + 1)
                            ),
                            Void.class
                    );
                } catch (Exception e) {
                    log.error("Failed to create document", e);
                }
            }
            Thread.sleep(1000);

            created += batch;
            log.info("Created {}/{} documents", created, count);
        }

        log.info(
                "Document generation finished in {} ms",
                System.currentTimeMillis() - start
        );
    }
}
