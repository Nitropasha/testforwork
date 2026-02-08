package org.dzhioev.ws.docservice.dao;

import org.dzhioev.ws.docservice.entity.DocumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentStatusHistoryRepository
        extends JpaRepository<DocumentStatusHistory, Long> {
}
