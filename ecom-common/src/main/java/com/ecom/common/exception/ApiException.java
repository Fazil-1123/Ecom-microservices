package com.ecom.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    private final String errorCode;

    @Serial
    private static final long serialVersionUID = 1L;


    public ApiException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

}
