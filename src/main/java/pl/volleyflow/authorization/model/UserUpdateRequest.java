package pl.volleyflow.authorization.model;

import javax.validation.constraints.Size;
import java.time.LocalDate;

public record UserUpdateRequest(

        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,

        LocalDate birthDate,

        @Size(max = 32) String phoneNumber,

        @Size(max = 120) String displayName,

        String avatar
) {
}
