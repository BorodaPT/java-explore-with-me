package ru.practicum.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(EwmException.class)
    protected ResponseEntity<Object> handleConflict(EwmException e) {
        return new ResponseEntity<>(e.getMessageException(), e.getStatus());
    }

}
