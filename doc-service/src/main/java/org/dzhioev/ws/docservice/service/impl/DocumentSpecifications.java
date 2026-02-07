package org.dzhioev.ws.docservice.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.dzhioev.ws.docservice.dto.DocumentSearchFilter;
import org.dzhioev.ws.docservice.entity.Document;
import org.dzhioev.ws.docservice.service.DocumentSpecifications;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecificationsImpl implements DocumentSpecifications {

    public static Specification<Document> byFilter(DocumentSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.author() != null && !filter.author().isBlank()) {
                predicates.add(cb.equal(root.get("author"), filter.author()));
            }

            if (filter.dateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.dateFrom().atStartOfDay()
                ));
            }

            if (filter.dateTo() != null) {
                predicates.add(cb.lessThan(
                        root.get("createdAt"),
                        filter.dateTo().plusDays(1).atStartOfDay()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
