package pl.volleyflow.user.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
        UUID externalId,
        String firstName,
        String lastName,
        String email,
        String avatarUrl
) {
}
