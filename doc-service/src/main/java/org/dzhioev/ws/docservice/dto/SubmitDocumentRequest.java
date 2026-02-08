package org.dzhioev.ws.docservice.dto;

public record SubmitDocumentRequest(
        String initiator,
        String comment
) {}
