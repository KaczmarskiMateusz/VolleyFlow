package pl.volleyflow.club.model;

import java.util.UUID;

public record ClubListDto(
        UUID clubExternalId,
        String ClubName,
        ClubRole clubRole
) {
}
