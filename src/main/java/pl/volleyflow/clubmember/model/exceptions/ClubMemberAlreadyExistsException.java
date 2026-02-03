package pl.volleyflow.clubmember.model.exceptions;

public class ClubMemberAlreadyExistsException extends RuntimeException {
    public ClubMemberAlreadyExistsException(String message) {
        super(message);
    }
}
