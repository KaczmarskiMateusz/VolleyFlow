package pl.volleyflow.clubmember.service;

import pl.volleyflow.clubmember.model.AddMemberRequest;
import pl.volleyflow.clubmember.model.MemberResponse;

import java.util.UUID;

public interface ClubMemberService {

    MemberResponse addOrInviteMember(UUID clubExternalId, AddMemberRequest req);

    void resendInvitation(UUID clubExternalId, UUID memberExternalId);

    void removeMember(UUID clubExternalId, UUID memberExternalId);

}
