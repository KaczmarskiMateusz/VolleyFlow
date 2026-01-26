package pl.volleyflow.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.user.model.GlobalRole;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserAccountMapper;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.repository.UserAccountRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserAccount registerUser(UserRegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        UserAccount user = UserAccountMapper.fromRegisterRequest(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setGlobalRole(GlobalRole.USER);
        userAccountRepository.save(user);

        return user;
    }

    @Override
    public UserAccount updateUser(UserAccount userAccount) {
        return null;
    }

    @Override
    public Optional<UserAccount> getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    public UserDto getUserByExternalId(UUID externalId) {
        UserAccount userAccount = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserAccountMapper.toDto(userAccount);
    }

}
