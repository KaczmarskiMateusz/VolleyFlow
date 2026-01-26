package pl.volleyflow.club.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record UpdateClubRequest(
        @NotBlank(message = "Club name is required")
        @Size(max = 160, message = "Club name must be at most 160 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Size(max = 120, message = "City must be at most 120 characters")
        String city,

        @Size(max = 2048, message = "Logo URL must be at most 2048 characters")
        @Pattern(regexp = "^$|https?://.+", message = "Logo URL must be empty or a valid URL")
        String logoUrl
) {
}
