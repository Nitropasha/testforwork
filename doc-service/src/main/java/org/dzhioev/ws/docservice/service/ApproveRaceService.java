package org.dzhioev.ws.docservice.service;

import org.dzhioev.ws.docservice.dto.ApproveRaceRequest;
import org.dzhioev.ws.docservice.dto.ApproveRaceResponse;

public interface ApproveRaceService {
    ApproveRaceResponse runRace(Long documentId, ApproveRaceRequest request) throws InterruptedException ;
}
