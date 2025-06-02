package lt.psk.bikerental.exception;

public class InvalidTripStateException extends RuntimeException {
    public InvalidTripStateException(String message) {
        super(message);
    }
}
