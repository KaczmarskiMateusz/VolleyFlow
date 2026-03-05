package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

public record AddMemberRequest(
        String email,
        String firstName,
        String lastName,
        String displayName,
        Boolean player,
        ClubRole role
) {
}
