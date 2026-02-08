package org.dzhioev.ws.docservice.dto;

import java.util.List;

public record BatchSubmitResponse(
        List<BatchItemResult> results
) {}