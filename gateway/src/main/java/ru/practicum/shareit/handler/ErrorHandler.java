package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.handler.exception.StateValidationException;
import ru.practicum.shareit.handler.response.StateErrorResponse;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleException(MethodArgumentNotValidException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " " + exception.getMessage());
    }

    @ExceptionHandler(StateValidationException.class)
    private ResponseEntity<StateErrorResponse> handleException(StateValidationException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StateErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<String> handleException(ValidationException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " " + exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<String> handleException() {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR - нарушение уникального индекса или первичного ключа");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpStatus.INTERNAL_SERVER_ERROR + " Нарушение уникального индекса или первичного ключа");
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> handleException(Exception exception) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpStatus.INTERNAL_SERVER_ERROR + exception.getMessage());
    }
}