package org.dzhioev.ws.docservice.dto;

import org.dzhioev.ws.docservice.enums.DocumentStatus;

public record ApproveRaceResponse(
        int successCount,
        int conflictCount,
        int errorCount,
        DocumentStatus finalStatus
) {}
