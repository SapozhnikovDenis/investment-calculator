package com.sapozhnikov.investment.calculator.utils.exceptions.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerInternalException extends InternalException {
    public InternalServerInternalException(String message) {
        super(message);
    }
}
