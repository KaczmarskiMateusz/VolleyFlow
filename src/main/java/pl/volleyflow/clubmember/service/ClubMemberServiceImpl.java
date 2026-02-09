package pl.volleyflow.clubmember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.exceptions.ClubNotFoundException;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.AddMemberRequest;
import pl.volleyflow.clubmember.model.ClubMember;
import pl.volleyflow.clubmember.model.MemberResponse;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberAlreadyExistsException;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private static final int TTL_FOR_INVITATION_DAYS = 7;

    private final ClubRepository clubRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Override
    @Transactional
    public MemberResponse addMember(UUID clubExternalId,
                                    AddMemberRequest request,
                                    UUID inviterExternalId) {
        UserAccount inviter = userAccountRepository.findByExternalId(inviterExternalId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with externalId " + inviterExternalId + " not found"));

        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException(
                        "Club with externalId " + clubExternalId + " not found"));


        if (clubMemberRepository.existsByClubIdAndInvitedEmail(club.getId(), normalizeEmail(request.email()))) {
            throw new ClubMemberAlreadyExistsException(
                    "Member with email " + request.email() + " already exists in club " + clubExternalId);
        }

        Optional<UserAccount> invitedUserOpt = userAccountRepository.findByEmail(normalizeEmail(request.email()));

        Instant expiresAt = Instant.now().plus(TTL_FOR_INVITATION_DAYS, ChronoUnit.DAYS);
        String invitationToken = UUID.randomUUID().toString();

        ClubMember clubMember = ClubMember.invite(
                club.getId(),
                request.role(),
                request.email(),
                invitationToken,
                expiresAt,
                inviter,
                invitedUserOpt.orElse(null)
        );

        clubMemberRepository.save(clubMember);

        log.info("Added club member memberExternalId={}, clubExternalId={}, invitedEmail={}, invitedByEmail={}",
                clubMember.getExternalId(), clubExternalId, request.email(), inviter.getEmail());

        return new MemberResponse(
                clubMember.getExternalId(),
                clubMember.getRole(),
                clubMember.getStatus(),
                inviter.getEmail(),
                invitedUserOpt.map(UserAccount::getExternalId).orElse(null)
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
