package org.dzhioev.ws.docservice.exeptions;

public class ConflictException extends ApiException {

    public ConflictException(String code, String message) {
        super(code, message);
    }
}
