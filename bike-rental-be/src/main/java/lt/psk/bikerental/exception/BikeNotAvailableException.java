package lt.psk.bikerental.exception;

public class BikeNotAvailableException extends RuntimeException {
    public BikeNotAvailableException(String message) {
        super(message);
    }
}
