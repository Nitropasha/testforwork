package org.dzhioev.ws.docservice.exeptions;

public class ValidationException extends ApiException {

    public ValidationException(String code, String message) {
        super(code, message);
    }
}