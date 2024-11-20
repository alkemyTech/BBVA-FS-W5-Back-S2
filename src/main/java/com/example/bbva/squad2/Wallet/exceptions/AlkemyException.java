package com.example.bbva.squad2.Wallet.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlkemyException extends RuntimeException {
    private final HttpStatus status;

    public AlkemyException(final HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public AlkemyException(final HttpStatus httpStatus, final String message) {
        super(message);
        this.status = httpStatus;
    }

}
