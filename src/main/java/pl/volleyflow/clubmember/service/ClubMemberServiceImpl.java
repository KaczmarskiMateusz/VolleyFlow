package pl.volleyflow.clubmember.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubNotFoundException;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.*;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private static final Duration INVITE_TTL = Duration.ofDays(7);

    private final ClubRepository clubRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClubMemberRepository clubMemberRepository;

    //TODO ADD THIS SERVICE
//    private final InvitationEmailSender invitationEmailSender;

    @Override
    @Transactional
    public MemberResponse addOrInviteMember(UUID clubExternalId, AddMemberRequest req) {
        String email = normalizeEmail(req.email());
        ClubRole role = req.role();

        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException(clubExternalId.toString()));

        // TODO: sprawdź uprawnienia current usera do dodawania w tym klubie

        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(email);

        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            Optional<ClubMember> existingMemberOpt = clubMemberRepository.findByClubAndUser(club, user);
            if (existingMemberOpt.isPresent()) {
                return toResponse(existingMemberOpt.get(), clubExternalId);
            }

            Optional<ClubMember> invitedExistingOpt =
                    clubMemberRepository.findByClubAndUserIsNullAndInvitedEmail(club, email);

            if (invitedExistingOpt.isPresent()) {
                ClubMember invitedExisting = invitedExistingOpt.get();
                invitedExisting.acceptInvite(user);
                return toResponse(invitedExisting, clubExternalId);
            }

            ClubMember newMember = ClubMember.builder()
                    .externalId(UUID.randomUUID())
                    .club(club)
                    .user(user)
                    .role(role)
                    .status(MembershipStatus.ACTIVE)
                    .invitedEmail(null)
                    .inviteToken(null)
                    .inviteExpiresAt(null)
                    .inviteAcceptedAt(Instant.now())
                    .invitedBy(null) // albo current user
                    .build();

            clubMemberRepository.save(newMember);
            return toResponse(newMember, clubExternalId);
        }

        // user nie istnieje -> zaproszenie
        Optional<ClubMember> existingInviteOpt =
                clubMemberRepository.findByClubAndUserIsNullAndInvitedEmail(club, email);

        String token = generateToken();
        Instant expiresAt = Instant.now().plus(INVITE_TTL);

        ClubMember clubMember;
        if (existingInviteOpt.isPresent()) {
            clubMember = existingInviteOpt.get();

            if (clubMember.getStatus() != MembershipStatus.INVITED) {
                throw new IllegalStateException("Existing record without user is not INVITED.");
            }

            clubMember.resendInvite(token, expiresAt);
        } else {
            clubMember = ClubMember.createInvited(
                    club,
                    role,
                    email,
                    token,
                    expiresAt,
                    null // invitedBy = current user
            );
            clubMemberRepository.save(clubMember);
        }

        return toResponse(clubMember, clubExternalId);
    }

    @Override
    @Transactional
    public void resendInvitation(UUID clubExternalId, UUID memberExternalId) {
        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException(clubExternalId.toString()));

        ClubMember clubMember = clubMemberRepository.findByClubAndExternalId(club, memberExternalId)
                .orElseThrow(() -> new ClubMemberNotFoundException(memberExternalId.toString()));

        if (clubMember.getStatus() != MembershipStatus.INVITED || clubMember.getUser() != null) {
            throw new IllegalStateException("Only INVITED member without user can be resent.");
        }
        if (clubMember.getInvitedEmail() == null) {
            throw new IllegalStateException("Invited email missing.");
        }

        String token = generateToken();
        Instant expiresAt = Instant.now().plus(INVITE_TTL);

        clubMember.resendInvite(token, expiresAt);

    }

    @Override
    @Transactional
    public void removeMember(UUID clubExternalId, UUID memberExternalId) {
        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException(clubExternalId.toString()));

        ClubMember clubMember = clubMemberRepository.findByClubAndExternalId(club, memberExternalId)
                .orElseThrow(() -> new ClubMemberNotFoundException(memberExternalId.toString()));

        clubMember.leave();
    }


    private static String normalizeEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email is required.");
        String normalized = email.trim().toLowerCase();
        if (normalized.isBlank()) throw new IllegalArgumentException("Email is required.");
        return normalized;
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static MemberResponse toResponse(ClubMember clubMember, UUID clubExternalId) {
        UUID userExternalId = null;
        if (clubMember.getUser() != null) {
            userExternalId = clubMember.getUser().getExternalId();
        }

//        return new MemberResponse(
//                clubMember.getExternalId(),
//                clubExternalId,
//                clubMember.getRole(),
//                clubMember.getStatus(),
//                clubMember.getInvitedEmail(),
//                userExternalId
//        );
        return null;
    }
}
