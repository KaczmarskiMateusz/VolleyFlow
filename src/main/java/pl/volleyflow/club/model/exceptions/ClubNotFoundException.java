package pl.volleyflow.club.model.exceptions;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException(String clubExternalId) {
        super("Club with external id '" + clubExternalId + "' do not exists");
    }
}
