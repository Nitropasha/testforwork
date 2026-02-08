package org.dzhioev.ws.docservice.dto;

import org.dzhioev.ws.docservice.enums.DocumentStatus;

import java.time.Instant;

public record DocumentResponse(
        Long id,
        String docNumber,
        String author,
        String title,
        DocumentStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
