package ru.practicum.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageException {

    private final String status;

    private final String reason;

    private final String message;

    private final LocalDateTime timestamp;

    public MessageException(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        timestamp = LocalDateTime.now().withNano(0);
    }
}
