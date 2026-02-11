package pl.volleyflow.authorization.model;

import lombok.Builder;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
public record UserUpdateRequest(

        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,

        LocalDate birthDate,

        @Size(max = 32) String phoneNumber,

        @Size(max = 120) String displayName,

        String avatar
) {
}
