package pl.volleyflow.user.service;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Login or password is invalid. Try again.");
    }
}
