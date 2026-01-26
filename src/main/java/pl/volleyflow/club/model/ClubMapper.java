package pl.volleyflow.club.model;

import java.util.Objects;

public class ClubMapper {

    public static ClubDto toDto(Club club) {
        if (club == null) return null;

        return ClubDto.builder()
                .externalId(club.getExternalId())
                .name(club.getName())
                .description(club.getDescription())
                .city(club.getCity())
                .logoUrl(club.getLogoUrl())
                .status(club.getStatus())
                .createdAt(club.getCreatedAt())
                .updatedAt(club.getUpdatedAt())
                .version(club.getVersion())
                .build();
    }

    public static Club fromCreateRequest(CreateClubRequest request) {
        Objects.requireNonNull(request, "request");

        return Club.builder()
                .name(trimToNull(request.name()))
                .description(trimToNull(request.description()))
                .city(trimToNull(request.city()))
                .logoUrl(trimToNull(request.logoUrl()))
                .status(ClubStatus.TO_CONFIRM)
                .build();
    }

    public static void applyUpdate(Club club, UpdateClubRequest request) {
        Objects.requireNonNull(club, "club");
        Objects.requireNonNull(request, "request");

        if (request.name() != null) {
            club.setName(trimToNull(request.name()));
        }
        if (request.description() != null) {
            club.setDescription(trimToNull(request.description()));
        }
        if (request.city() != null) {
            club.setCity(trimToNull(request.city()));
        }
        if (request.logoUrl() != null) {
            club.setLogoUrl(trimToNull(request.logoUrl()));
        }
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
