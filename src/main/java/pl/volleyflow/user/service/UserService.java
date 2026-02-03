package pl.volleyflow.user.service;

import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.model.UserUpdateRequest;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserAccount registerUser(UserRegisterRequest userAccount);

    Optional<UserAccount> getUserByEmail(String email);

    UserDto getUserByExternalId(UUID externalId);

    UserDto putUser(UserUpdateRequest request, UUID uuid);

    UserDto patchUser(UserUpdateRequest request, UUID uuid);

    void deleteUser(UUID externalId);
}
