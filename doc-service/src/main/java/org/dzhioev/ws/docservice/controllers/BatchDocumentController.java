package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.BatchSubmitRequest;
import org.dzhioev.ws.docservice.dto.BatchSubmitResponse;
import org.dzhioev.ws.docservice.service.BatchDocumentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents/batch")
public class BatchDocumentController {

    private final BatchDocumentService batchDocumentService;

    public BatchDocumentController(BatchDocumentService batchDocumentService) {
        this.batchDocumentService = batchDocumentService;
    }

    @PostMapping("/submit")
    public BatchSubmitResponse submit(@RequestBody BatchSubmitRequest request) {
        return batchDocumentService.batchSubmit(request);
    }
}
