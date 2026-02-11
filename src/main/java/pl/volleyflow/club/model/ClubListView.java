package pl.volleyflow.club.model;

import java.time.Instant;
import java.util.UUID;

public record ClubListView(
        UUID externalId,
        String getName,
        Instant getCreatedAt,
        String getLogoUrl,
        String getRole) {
}
