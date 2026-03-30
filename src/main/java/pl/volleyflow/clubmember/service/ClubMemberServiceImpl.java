package pl.volleyflow.clubmember.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.club.model.exceptions.ClubNotFoundException;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.*;
import pl.volleyflow.clubmember.model.memberprofile.MemberProfile;
import pl.volleyflow.clubmember.model.memberprofile.MemberResponse;
import pl.volleyflow.clubmember.model.memberprofile.MembershipStatus;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberAlreadyExistsException;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberForbiddenException;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberValidationException;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.clubmember.repository.MemberProfileRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

import java.time.Instant;
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
        validateInputs(clubExternalId, request, inviterExternalId);

        Club club = clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException(clubExternalId.toString()));

        UserAccount inviter = userAccountRepository.findByExternalIdAndDeletedFalse(inviterExternalId)
                .orElseThrow(() -> new UserNotFoundException("Inviter user not found: " + inviterExternalId));

        ensureOwner(inviter, club);

        String email = normalizeEmail(request.email());
        ensureProfileDataIsPresent(request, email);

        MemberProfile profile = resolveProfile(email);
        enrichProfile(profile, request, email);
        profile = memberProfileRepository.save(profile);

        ensureNotAlreadyClubMember(club, profile);

        ClubMember membership = buildMembership(club, profile, request, inviterExternalId, email);
        ClubMember saved = clubMemberRepository.save(membership);

        UUID linkedUserExternalId = profile.getUserAccount() == null ? null : profile.getUserAccount().getExternalId();

        log.info("Added member clubExternalId={}, profileExternalId={}, status={}",
                clubExternalId, profile.getExternalId(), saved.getStatus());

        return mapToResponse(saved, profile, linkedUserExternalId);
    }

    private void validateInputs(UUID clubExternalId, AddMemberRequest request, UUID inviterExternalId) {
        if (clubExternalId == null) {
            throw new ClubMemberValidationException("clubExternalId is required");
        }
        if (request == null) {
            throw new ClubMemberValidationException("request is required");
        }
        if (inviterExternalId == null) {
            throw new ClubMemberValidationException("inviterExternalId is required");
        }
    }

    private void ensureOwner(UserAccount inviter, Club club) {
        boolean isOwner = clubMemberRepository.findUserRoleInClub(club.getId(), inviter.getId(), ClubRole.OWNER.name());
        if (!isOwner) {
            throw new ClubMemberForbiddenException("No permissions to add members");
        }
    }

    private String normalizeEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private void ensureProfileDataIsPresent(AddMemberRequest request, String email) {
        if (StringUtils.isNotBlank(email)) {
            return;
        }

        if (StringUtils.isBlank(request.displayName())
                && StringUtils.isBlank(request.firstName())
                && StringUtils.isBlank(request.lastName())) {
            log.info("Missing profile data, email is blank and displayName/firstName/lastName are empty."
                            + " displayName={}, firstName={}, lastName={}",
                    request.displayName(), request.firstName(), request.lastName());
            throw new ClubMemberValidationException("Provide at least email or name/displayName");
        }
    }

    private MemberProfile resolveProfile(String email) {
        if (StringUtils.isBlank(email)) {
            return new MemberProfile();
        }
        return memberProfileRepository.findByContactEmailIgnoreCase(email)
                .orElseGet(MemberProfile::new);
    }

    private void enrichProfile(MemberProfile profile, AddMemberRequest request, String email) {
        if (StringUtils.isBlank(profile.getContactEmail())) {
            profile.setContactEmail(email);
        }
        if (!StringUtils.isBlank(request.firstName())) {
            profile.setFirstName(request.firstName());
        }
        if (!StringUtils.isBlank(request.lastName())) {
            profile.setLastName(request.lastName());
        }
        if (!StringUtils.isBlank(request.displayName())) {
            profile.setDisplayName(request.displayName());
        }
    }

    private void ensureNotAlreadyClubMember(Club club, MemberProfile profile) {
        if (clubMemberRepository.existsByClubIdAndProfileId(club.getId(), profile.getId())) {
            throw new ClubMemberAlreadyExistsException("This profile is already a member of this club");
        }
    }

    private ClubMember buildMembership(Club club,
                                       MemberProfile profile,
                                       AddMemberRequest request,
                                       UUID inviterExternalId,
                                       String email) {
        ClubRole role = request.role() == null ? ClubRole.MEMBER : request.role();
        boolean player = request.player() == null || request.player();

        ClubMember membership = new ClubMember();
        membership.setClub(club);
        membership.setMemberProfile(profile);
        membership.setRole(role);
        membership.setPlayer(player);
        membership.setCreatedByUserExternalId(inviterExternalId);

        boolean linkedToAccount = profile.getUserAccount() != null;
        if (!linkedToAccount && email != null) {
            membership.setStatus(MembershipStatus.INVITED);
        } else {
            membership.setStatus(MembershipStatus.ACTIVE);
            membership.setJoinedAt(Instant.now());
        }

        return membership;
    }

    private MemberResponse mapToResponse(ClubMember saved, MemberProfile profile, UUID linkedUserExternalId) {
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
