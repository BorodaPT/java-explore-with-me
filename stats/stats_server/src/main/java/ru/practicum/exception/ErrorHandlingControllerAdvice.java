package ru.practicum.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(StatsException.class)
    protected ResponseEntity<Object> handleConflict(StatsException e) {
        return new ResponseEntity<>(e.getMessageException(), e.getStatus());
    }

}
