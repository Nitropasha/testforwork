package org.dzhioev.ws.docservice.dto;

import org.dzhioev.ws.docservice.enums.BatchResultStatus;

public record BatchItemResult(
        Long documentId,
        BatchResultStatus status,
        String message
) {}