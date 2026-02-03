package pl.volleyflow.user.service.exceptions;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Login or password for is invalid. Try again.");
    }
}
