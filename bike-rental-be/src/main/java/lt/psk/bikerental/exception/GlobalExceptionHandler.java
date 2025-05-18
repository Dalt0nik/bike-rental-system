package lt.psk.bikerental.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static String formatExceptionToString(Exception ex) {
        StringBuilder exceptionStringBuilder = new StringBuilder();
        exceptionStringBuilder.append(ex.getMessage());

        exceptionStringBuilder.append("\n");
        exceptionStringBuilder.append(ex.getCause());

        for (StackTraceElement el : ex.getStackTrace()) {
            exceptionStringBuilder.append("\n\t\t");
            exceptionStringBuilder.append(el.toString());
        }

        exceptionStringBuilder.append("\n");

        return exceptionStringBuilder.toString();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error(formatExceptionToString(ex));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(formatExceptionToString(ex));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("This bike was taken by another user. Please refresh and try again.");
    }
}
