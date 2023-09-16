package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StatsException extends RuntimeException {

    private final MessageException messageException;

    private final HttpStatus status;

    public StatsException(String reason, String message, HttpStatus status) {
        super();
        this.messageException = new MessageException(status.toString(), reason, message);
        this.status = status;
    }

}
