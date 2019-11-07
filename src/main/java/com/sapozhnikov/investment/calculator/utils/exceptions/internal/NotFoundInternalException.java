package com.sapozhnikov.investment.calculator.utils.exceptions.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundInternalException extends InternalException {
    public NotFoundInternalException(String message) {
        super(message);
    }
}
