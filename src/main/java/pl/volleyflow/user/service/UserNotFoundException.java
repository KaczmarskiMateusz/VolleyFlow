package pl.volleyflow.user.service;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("Email do not exists: " + email);
    }

}
