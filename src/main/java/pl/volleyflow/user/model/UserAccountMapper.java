package pl.volleyflow.user.model;

import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.model.UserUpdateRequest;

public final class UserAccountMapper {

    public static UserAccount fromRegisterRequest(UserRegisterRequest request) {
        if (request == null) return null;

        return UserAccount.builder()
                .email(request.email())
                .birthDate(request.birthDate())
                .avatarUrl(request.avatar())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .displayName(request.displayName())
                .build();
    }

    public static UserAccount applyPatch(UserAccount existing, UserUpdateRequest request) {
        if (existing == null || request == null) return existing;

        if (request.firstName() != null) existing.setFirstName(request.firstName());
        if (request.lastName() != null) existing.setLastName(request.lastName());
        if (request.phoneNumber() != null) existing.setPhoneNumber(request.phoneNumber());
        if (request.avatar() != null) existing.setAvatarUrl(request.avatar());
        if (request.displayName() != null) existing.setDisplayName(request.displayName());
        if (request.birthDate() != null) existing.setBirthDate(request.birthDate());

        return existing;
    }

    public static void applyPut(UserAccount existing, UserUpdateRequest request) {
        if (existing == null || request == null) return;

        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setPhoneNumber(request.phoneNumber());
        existing.setAvatarUrl(request.avatar());
        existing.setDisplayName(request.displayName());
        existing.setBirthDate(request.birthDate());
    }

    public static UserDto toDto(UserAccount u) {
        if (u == null) return null;

        return UserDto.builder()
                .email(u.getEmail())
                .externalId(u.getExternalId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .avatarUrl(u.getAvatarUrl())
                .build();
    }

}
