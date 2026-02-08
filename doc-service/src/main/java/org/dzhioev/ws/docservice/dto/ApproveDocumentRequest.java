package org.dzhioev.ws.docservice.dto;

public record ApproveDocumentRequest(
        String initiator,
        String comment
) {}
