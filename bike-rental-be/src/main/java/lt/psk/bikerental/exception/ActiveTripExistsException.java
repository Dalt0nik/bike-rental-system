package lt.psk.bikerental.exception;

public class ActiveTripExistsException extends RuntimeException {
    public ActiveTripExistsException(String message) {
        super(message);
    }
}