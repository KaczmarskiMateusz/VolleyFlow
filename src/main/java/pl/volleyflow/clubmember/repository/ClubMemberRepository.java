package pl.volleyflow.clubmember.repository;

import org.springframework.data.repository.CrudRepository;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.clubmember.model.ClubMember;
import pl.volleyflow.user.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {

    Optional<ClubMember> findByClubAndUser(Club club, UserAccount user);

    Optional<ClubMember> findByClubAndUserIsNullAndInvitedEmail(Club club, String invitedEmail);

    Optional<ClubMember> findByClubAndExternalId(Club club, UUID externalId);

    Optional<ClubMember> findByClubExternalIdAndUserExternalId(UUID clubExternalId, UUID userExternalId);

    Optional<ClubMember> findByInviteToken(String inviteToken);

}
