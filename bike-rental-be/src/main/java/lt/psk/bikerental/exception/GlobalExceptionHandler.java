package lt.psk.bikerental.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    @ExceptionHandler({
            BikeNotAvailableException.class,
            InvalidBookingException.class,
            ActiveTripExistsException.class
    })
    public ResponseEntity<String> handleTripValidationExceptions(RuntimeException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        String message = "This record was updated by someone else. "
                + "Please refresh to get the latest data, merge your changes, and try again.";
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }
}
