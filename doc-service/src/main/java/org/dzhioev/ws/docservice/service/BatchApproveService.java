package org.dzhioev.ws.docservice.service;

import org.dzhioev.ws.docservice.dto.BatchApproveRequest;
import org.dzhioev.ws.docservice.dto.BatchApproveResponse;

public interface BatchApproveService {
    BatchApproveResponse batchApprove(BatchApproveRequest request);
}
