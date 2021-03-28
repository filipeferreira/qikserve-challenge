package com.qikserve.supermarket.checkout.exceptionhandler;

import com.qikserve.supermarket.checkout.excetions.ProductException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
        List<Error> erros = Arrays.asList(new Error(getUserMessage(ex.getMessage()), getDeveloperMessage(ex)));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), ex.getStatus(), request);
    }

    private String getDeveloperMessage(ProductException ex) {
        return ex.getCause() != null ? ex.getCause().toString() : ex.toString();
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Object> handleSocketTimeoutException(SocketTimeoutException ex, WebRequest request) {
        String userMessage = getUserMessage("wiremockserver.timeout");
        List<Error> errors = Arrays.asList(new Error(userMessage, ex.toString()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.REQUEST_TIMEOUT, request);
    }

    private String getUserMessage(String s) {
        return messageSource.getMessage(s, null, LocaleContextHolder.getLocale());
    }

    @Data
    @AllArgsConstructor
    public static class Error {

        private final String userMessage;
        private final String developerMessage;

    }
}
