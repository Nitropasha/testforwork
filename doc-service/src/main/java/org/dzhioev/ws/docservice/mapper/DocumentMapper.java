package org.dzhioev.ws.docservice.mapper;

import org.dzhioev.ws.docservice.dto.DocumentResponse;
import org.dzhioev.ws.docservice.entity.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentResponse toResponse(Document document);
}