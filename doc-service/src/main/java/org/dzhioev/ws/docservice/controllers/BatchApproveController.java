package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.BatchApproveRequest;
import org.dzhioev.ws.docservice.dto.BatchApproveResponse;
import org.dzhioev.ws.docservice.service.BatchApproveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents/batch")
public class BatchApproveController {

    private final BatchApproveService batchApproveService;

    public BatchApproveController(BatchApproveService batchApproveService) {
        this.batchApproveService = batchApproveService;
    }

    @PostMapping("/approve")
    public BatchApproveResponse approve(@RequestBody BatchApproveRequest request) {
        return batchApproveService.batchApprove(request);
    }
}
