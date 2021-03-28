package com.qikserve.supermarket.checkout.exceptionhandler;

import com.qikserve.supermarket.checkout.excetions.ProductException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class CheckoutExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public CheckoutExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(ProductException ex, WebRequest request) {
        String userMessage = messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
        String developerMessage = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<Error> erros = Arrays.asList(new Error(userMessage, developerMessage));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), ex.getStatus(), request);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Object> handleSocketTimeoutException(SocketTimeoutException ex, WebRequest request) {
        String userMessage = messageSource.getMessage("wiremockserver.timeout", null, LocaleContextHolder.getLocale());
        String developerMessage = ex.toString();
        List<Error> errors = Arrays.asList(new Error(userMessage, developerMessage));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.REQUEST_TIMEOUT, request);
    }

    @Data
    @AllArgsConstructor
    public static class Error {

        private final String userMessage;
        private final String developerMessage;

    }
}
