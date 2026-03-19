package pl.volleyflow.authorization.model;

import lombok.Builder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Builder
public record UserRegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,

        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,

        LocalDate birthDate,

        @Size(max = 32) String phoneNumber,

        @Size(max = 120) String displayName,

        String avatar
) {
}
