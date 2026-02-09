package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.BatchApproveRequest;
import org.dzhioev.ws.docservice.dto.BatchApproveResponse;
import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.dto.BatchSubmitResponse;
import org.dzhioev.ws.docservice.service.BatchApproveService;
import org.dzhioev.ws.docservice.service.BatchSubmitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents/batch")
public class BatchDocumentController {

    private final BatchSubmitService batchDocumentService;
    private final BatchApproveService batchApproveService;

    public BatchDocumentController(BatchSubmitService batchDocumentService, BatchApproveService batchApproveService) {
        this.batchDocumentService = batchDocumentService;
        this.batchApproveService = batchApproveService;
    }

    @PostMapping("/submit")
    public BatchSubmitResponse submit(@RequestBody BatchSubmitRequest request) {
        return batchDocumentService.batchSubmit(request);
    }

    @PostMapping("/approve")
    public BatchApproveResponse approve(@RequestBody BatchApproveRequest request) {
        return batchApproveService.batchApprove(request);
    }
}
