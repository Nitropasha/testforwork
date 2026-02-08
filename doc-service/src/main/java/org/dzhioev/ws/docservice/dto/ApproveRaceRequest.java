package org.dzhioev.ws.docservice.dto;

public record ApproveRaceRequest(
        int threads,
        int attempts,
        String initiator
) {}
