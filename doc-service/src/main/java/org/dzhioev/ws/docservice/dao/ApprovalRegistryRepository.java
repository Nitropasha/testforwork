package org.dzhioev.ws.docservice.dao;

import org.dzhioev.ws.docservice.entity.ApprovalRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalRegistryRepository
        extends JpaRepository<ApprovalRegistry, Long> {
    Optional<ApprovalRegistry> findByDocumentId(Long documentId);
}
