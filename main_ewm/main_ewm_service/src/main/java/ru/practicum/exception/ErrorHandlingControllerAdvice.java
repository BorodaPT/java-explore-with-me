package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Map<String, MessageException> handleConflict(MethodArgumentNotValidException e) {
        Map<String, MessageException> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            MessageException messageException = new MessageException(HttpStatus.BAD_REQUEST.toString(), "Not valid data", error.getDefaultMessage());
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, messageException);
        });
        return errors;
    }

    @ExceptionHandler(EwmException.class)
    protected ResponseEntity<Object> handleConflict(EwmException e) {
        return new ResponseEntity<>(e.getMessageException(), e.getStatus());
    }

}
