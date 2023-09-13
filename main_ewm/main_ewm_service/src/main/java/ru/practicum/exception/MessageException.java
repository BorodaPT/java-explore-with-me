package ru.practicum.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageException {

    private String status;

    private String reason;

    private String message;

    private LocalDateTime timestamp;

    public MessageException(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        timestamp = LocalDateTime.now().withNano(0);
    }
}
