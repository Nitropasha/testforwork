package org.dzhioev.ws.docservice.service;

import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.dto.BatchSubmitResponse;

public interface BatchDocumentService {
    BatchSubmitResponse batchSubmit(BatchSubmitRequest request);
}
