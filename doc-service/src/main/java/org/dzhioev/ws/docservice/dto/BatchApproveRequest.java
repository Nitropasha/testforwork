package org.dzhioev.ws.docservice.dto;

import java.util.List;

public record BatchApproveRequest(
        String initiator,
        String comment,
        List<Long> documentIds
) {
}
