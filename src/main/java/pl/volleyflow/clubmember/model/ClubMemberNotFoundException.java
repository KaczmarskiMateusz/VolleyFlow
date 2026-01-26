package pl.volleyflow.clubmember.model;

public class ClubMemberNotFoundException extends RuntimeException {
    public ClubMemberNotFoundException(String externalId) {
        super("Club member with external id '" + externalId + "' do not exists");
    }
}
