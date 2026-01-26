package pl.volleyflow.user.service;

import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    
    UserAccount registerUser(UserRegisterRequest userAccount);

    UserAccount updateUser(UserAccount userAccount);

    Optional<UserAccount> getUserByEmail(String email);

    UserDto getUserByExternalId(UUID externalId);

}
