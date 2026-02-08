package org.dzhioev.ws.docgenerator.dto;

import java.util.List;

public record BatchSubmitRequest(
        String initiator,
        String comment,
        List<Long> documentIds
) {}
