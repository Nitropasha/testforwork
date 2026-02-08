package org.dzhioev.ws.docservice.exeptions;

public class NotFoundException extends ApiException {

    public NotFoundException(String code, String message) {
        super(code, message);
    }
}

