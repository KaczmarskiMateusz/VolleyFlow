package pl.volleyflow.club.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ClubDto(
        UUID externalId,
        String name,
        String description,
        String city,
        String logoUrl,
        ClubStatus status
) {
}
