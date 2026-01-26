package pl.volleyflow.clubmember.model;

public class ClubMemberForbiddenException extends RuntimeException {
    public ClubMemberForbiddenException(String message) {
        super(message);
    }
}
