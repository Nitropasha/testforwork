package org.dzhioev.ws.docservice.controllers;

import org.dzhioev.ws.docservice.dto.ApproveRaceRequest;
import org.dzhioev.ws.docservice.dto.ApproveRaceResponse;
import org.dzhioev.ws.docservice.service.ApproveRaceService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class ApproveRaceController {

    private final ApproveRaceService approveRaceService;

    public ApproveRaceController(ApproveRaceService approveRaceService) {
        this.approveRaceService = approveRaceService;
    }

    @PostMapping("/{id}/approve-race")
    public ApproveRaceResponse approveRace(
            @PathVariable Long id,
            @RequestBody ApproveRaceRequest request
    ) throws InterruptedException {
        return approveRaceService.runRace(id, request);
    }
}
