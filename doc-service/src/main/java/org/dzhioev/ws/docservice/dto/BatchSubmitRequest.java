package org.dzhioev.ws.docservice.dto;

import java.util.List;

public record BatchSubmitRequest(
        String initiator,
        String comment,
        List<Long> documentIds
) {}