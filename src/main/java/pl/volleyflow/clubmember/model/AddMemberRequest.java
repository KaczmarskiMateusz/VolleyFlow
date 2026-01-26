package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

public record AddMemberRequest(
        String email,
        ClubRole role
) {}