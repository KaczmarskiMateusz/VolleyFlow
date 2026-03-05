package pl.volleyflow.user.model;

import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.model.UserUpdateRequest;
import pl.volleyflow.clubmember.model.MemberProfile;

public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccount fromRegisterRequest(UserRegisterRequest request) {
        if (request == null) return null;

        String email = request.email() == null ? null : request.email().trim().toLowerCase();

        return UserAccount.builder()
                .loginEmail(email)
                .deleted(false)
                .build();
    }

    public static void applyPatch(UserAccount existing, UserUpdateRequest request) {
        if (existing == null || request == null) return;

        MemberProfile profile = existing.getMemberProfile();
        if (profile == null) return;

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.lastName() != null) profile.setLastName(request.lastName());
        if (request.phoneNumber() != null) profile.setPhoneNumber(request.phoneNumber());
        if (request.avatar() != null) profile.setAvatarUrl(request.avatar());
        if (request.displayName() != null) profile.setDisplayName(request.displayName());
        if (request.birthDate() != null) profile.setBirthDate(request.birthDate());
    }

    public static void applyPut(UserAccount existing, UserUpdateRequest request) {
        if (existing == null || request == null) return;

        MemberProfile profile = existing.getMemberProfile();
        if (profile == null) return;

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setAvatarUrl(request.avatar());
        profile.setDisplayName(request.displayName());
        profile.setBirthDate(request.birthDate());
    }

    public static UserDto toDto(UserAccount u) {
        if (u == null) return null;

        MemberProfile p = u.getMemberProfile();

        return UserDto.builder()
                .externalId(u.getExternalId())
                .email(u.getLoginEmail())
                .firstName(p == null ? null : p.getFirstName())
                .lastName(p == null ? null : p.getLastName())
                .avatarUrl(p == null ? null : p.getAvatarUrl())
                .build();
    }

}
