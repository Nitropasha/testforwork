package org.dzhioev.ws.docservice.dto;

import org.dzhioev.ws.docservice.enums.DocumentStatus;

import java.time.LocalDate;

public record DocumentSearchFilter(
        DocumentStatus status,
        String author,
        LocalDate dateFrom,
        LocalDate dateTo
) {}
