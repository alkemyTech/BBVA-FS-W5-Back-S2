package com.example.bbva.squad2.Wallet.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WalletsException extends RuntimeException {
    private final HttpStatus status;

    public WalletsException(final HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public WalletsException(final HttpStatus httpStatus, final String message) {
        super(message);
        this.status = httpStatus;
    }

}
