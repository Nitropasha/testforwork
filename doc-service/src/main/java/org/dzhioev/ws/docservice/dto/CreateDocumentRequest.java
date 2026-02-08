package org.dzhioev.ws.docservice.dto;

public record CreateDocumentRequest(
        String author,
        String title
) {}