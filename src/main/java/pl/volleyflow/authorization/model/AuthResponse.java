package pl.volleyflow.authorization.model;

import pl.volleyflow.user.model.UserDto;

import java.util.List;

public record AuthResponse(
        UserDto userDto,
        String token,
        boolean success,
        List<String> messages
) {
    public static AuthResponse failed(List<String> messages) {
        return new AuthResponse(null, null, false, messages);
    }

    public static AuthResponse success(UserDto userDto, String token) {
        return new AuthResponse(userDto, token, true, null);
    }

}
