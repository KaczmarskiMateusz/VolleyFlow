package pl.volleyflow.clubmember.model;

import pl.volleyflow.club.model.ClubRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record AddMemberRequest(
        @Email
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "First name is required")
        @Size(max = 64, message = "First name must be at most 64 characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 64, message = "Last name must be at most 64 characters")
        String lastName,
        @NotBlank(message = "Display name is required")
        @Size(max = 120, message = "Display name must be at most 120 characters")
        String displayName,
        Boolean player,
        @NotBlank(message = "Role is required")
        ClubRole role) {
}
