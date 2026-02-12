package pl.volleyflow.club.model;

import java.time.Instant;
import java.util.UUID;

public record ClubListView(
        UUID externalId,
        String name,
        Instant createdAt,
        String logoUrl,
        String role) {
}
