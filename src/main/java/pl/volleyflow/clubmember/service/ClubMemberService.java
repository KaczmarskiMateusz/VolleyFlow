package pl.volleyflow.clubmember.service;

import pl.volleyflow.clubmember.model.AddMemberRequest;
import pl.volleyflow.clubmember.model.memberprofile.MemberResponse;

import java.util.UUID;

public interface ClubMemberService {

    MemberResponse addMember(UUID clubExternalId, AddMemberRequest req, UUID userExternalId);

}
