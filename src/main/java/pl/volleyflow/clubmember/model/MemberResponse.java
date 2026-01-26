package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

import java.util.UUID;

public record MemberResponse(
        UUID memberExternalId,
        ClubRole role,
        MembershipStatus status,
        String invitedEmail,
        UUID userExternalId
) {}