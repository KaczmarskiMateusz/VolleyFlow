package pl.volleyflow.club.model.exceptions;

public class ClubAlreadyExistsException extends RuntimeException {

    public ClubAlreadyExistsException(String name) {
        super("Club with name '" + name + "' already exists");
    }

}
