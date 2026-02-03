package pl.volleyflow.clubmember.model.exceptions;

public class ClubMemberForbiddenException extends RuntimeException {
    public ClubMemberForbiddenException(String message) {
        super(message);
    }
}
