package pl.volleyflow.clubmember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.*;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.clubmember.repository.MemberProfileRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private final ClubRepository clubRepository;
    private final UserAccountRepository userAccountRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Override
    @Transactional
    public MemberResponse addMember(UUID clubExternalId,
                                    AddMemberRequest request,
                                    UUID inviterExternalId) {
        if (clubExternalId == null) throw new IllegalArgumentException("clubExternalId is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (inviterExternalId == null) throw new IllegalArgumentException("inviterExternalId is required");

        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found: " + clubExternalId));

        UserAccount inviter = userAccountRepository.findByExternalId(inviterExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Inviter user not found: " + inviterExternalId));

        boolean isOwner = clubMemberRepository.findUserRoleInClub(club.getId(), inviter.getId(), ClubRole.OWNER.name());
        if (!isOwner) {
            throw new IllegalArgumentException("No permissions to add members");
        }

        String email = request.email() == null ? null : request.email().trim().toLowerCase();
        if (email != null && email.isBlank()) email = null;

        if (email == null
                && (request.displayName() == null || request.displayName().isBlank())
                && (request.firstName() == null || request.firstName().isBlank())
                && (request.lastName() == null || request.lastName().isBlank())) {
            throw new IllegalArgumentException("Provide at least email or name/displayName");
        }

        MemberProfile profile = (email == null)
                ? new MemberProfile()
                : memberProfileRepository.findByContactEmailIgnoreCase(email).orElseGet(MemberProfile::new);

        // Fill only missing fields when profile already exists.
        if (profile.getType() == null) profile.setType("PERSON");
        if (profile.getContactEmail() == null) profile.setContactEmail(email);

        if (request.firstName() != null && (profile.getFirstName() == null || profile.getFirstName().isBlank())) {
            profile.setFirstName(request.firstName());
        }
        if (request.lastName() != null && (profile.getLastName() == null || profile.getLastName().isBlank())) {
            profile.setLastName(request.lastName());
        }
        if (request.displayName() != null && (profile.getDisplayName() == null || profile.getDisplayName().isBlank())) {
            profile.setDisplayName(request.displayName());
        }

        profile = memberProfileRepository.save(profile);

        if (clubMemberRepository.existsByClubIdAndProfileId(club.getId(), profile.getId())) {
            throw new IllegalArgumentException("This profile is already a member of this club");
        }

        ClubRole role = request.role() == null ? ClubRole.MEMBER : request.role();
        boolean player = request.player() == null || request.player();

        ClubMember membership = new ClubMember();
        membership.setClub(club);
        membership.setMemberProfile(profile);
        membership.setRole(role);
        membership.setPlayer(player);
        membership.setCreatedByUserExternalId(inviterExternalId);

        // If the profile isn't linked to a user account yet, treat email-based add as invitation.
        boolean linkedToAccount = profile.getUserAccount() != null;
        if (!linkedToAccount && email != null) {
            membership.setStatus(MembershipStatus.INVITED);
        } else {
            membership.setStatus(MembershipStatus.ACTIVE);
            membership.setJoinedAt(java.time.Instant.now());
        }

        ClubMember saved = clubMemberRepository.save(membership);

        UUID linkedUserExternalId = profile.getUserAccount() == null ? null : profile.getUserAccount().getExternalId();

        log.info("Added member clubExternalId={}, profileExternalId={}, status={}",
                clubExternalId, profile.getExternalId(), saved.getStatus());

        return new MemberResponse(
                saved.getExternalId(),
                profile.getExternalId(),
                profile.getDisplayName(),
                profile.getContactEmail(),
                saved.getRole(),
                saved.getStatus(),
                linkedUserExternalId
        );
    }

}
