package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

import java.util.UUID;

public record MemberResponse(
        UUID membershipExternalId,
        UUID profileExternalId,
        String displayName,
        String contactEmail,
        ClubRole role,
        MembershipStatus status,
        UUID linkedUserExternalId) {
}
