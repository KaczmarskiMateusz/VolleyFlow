package pl.volleyflow.user.model;

public final class UserAccountMapper {

    public static UserAccount fromRegisterRequest(UserRegisterRequest u) {
        if (u == null) return null;

        return UserAccount.builder()
                .email(u.email())
                .birthDate(u.birthDate())
                .avatarUrl(u.avatar())
                .firstName(u.firstName())
                .lastName(u.lastName())
                .phoneNumber(u.phoneNumber())
                .displayName(u.displayName())
                .build();
    }

    public static UserDto toDto(UserAccount u) {
        if (u == null) return null;

        return UserDto.builder()
                .email(u.getEmail())
                .externalId(u.getExternalId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .avatar(u.getAvatarUrl())
                .build();
    }

}
