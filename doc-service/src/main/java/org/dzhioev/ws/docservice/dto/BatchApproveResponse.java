package org.dzhioev.ws.docservice.dto;

import java.util.List;

public record BatchApproveResponse(
        List<BatchItemResult> results
) {}
