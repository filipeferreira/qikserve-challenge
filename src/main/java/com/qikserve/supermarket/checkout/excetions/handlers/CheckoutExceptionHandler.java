package com.qikserve.supermarket.checkout.excetions.handlers;

import com.qikserve.supermarket.checkout.excetions.ProductException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
public class CheckoutExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public CheckoutExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        List<Error> errors = Arrays.asList(new Error(getUserMessage("invalid.request"), getDeveloperMessage(ex)));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Error> errors = Arrays.asList(new Error(getUserMessage("invalid.argument"), getDeveloperMessage(ex)));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<Error> errors = getErrorsList(ex.getBindingResult());
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<Error> getErrorsList(BindingResult bindingResult) {
        List<Error> errors = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.add(new Error(messageSource.getMessage(fieldError, Locale.UK), fieldError.toString()));
        }

        return errors;
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        List<Error> errors = Arrays.asList(new Error(getUserMessage(ex.getReason()), getDeveloperMessage(ex)));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), ex.getStatus(), request);
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Object> handleProductException(ProductException ex, WebRequest request) {
        List<Error> errors = Arrays.asList(new Error(getUserMessage(ex.getMessage()), getDeveloperMessage(ex)));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), ex.getStatus(), request);
    }

    private String getDeveloperMessage(Exception ex) {
        return ex.getCause() != null ? ex.getCause().toString() : ex.toString();
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Object> handleSocketTimeoutException(Exception ex, WebRequest request) {
        String userMessage = getUserMessage("wiremockserver.timeout");
        List<Error> errors = Arrays.asList(new Error(userMessage, ex.toString()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.REQUEST_TIMEOUT, request);
    }

    private String getUserMessage(String s) {
        return messageSource.getMessage(s, null, Locale.UK);
    }

    @Data
    @AllArgsConstructor
    public static class Error {

        private final String userMessage;
        private final String developerMessage;

    }
}
