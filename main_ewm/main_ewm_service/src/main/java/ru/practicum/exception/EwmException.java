package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EwmException extends RuntimeException {

    private MessageException messageException;

    private HttpStatus status;

    public EwmException(String reason, String message, HttpStatus status) {
        super();
        this.messageException = new MessageException(status.toString(), reason, message);
        this.status = status;
    }

}
