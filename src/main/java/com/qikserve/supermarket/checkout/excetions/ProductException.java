package com.qikserve.supermarket.checkout.excetions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ProductException extends Exception {

    private HttpStatus status;

    public ProductException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
