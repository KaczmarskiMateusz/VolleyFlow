package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AddMemberRequest(
        @Email
        @Size(max = 320, message = "Email must be at most 320 characters")
        String email,
        @Size(max = 64, message = "First name must be at most 64 characters")
        String firstName,
        @Size(max = 64, message = "Last name must be at most 64 characters")
        String lastName,
        @Size(max = 120, message = "Display name must be at most 120 characters")
        String displayName,
        Boolean player,
        ClubRole role) {
}
