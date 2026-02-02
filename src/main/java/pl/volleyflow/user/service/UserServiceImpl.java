package pl.volleyflow.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.model.UserUpdateRequest;
import pl.volleyflow.user.model.GlobalRole;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserAccountMapper;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.repository.UserAccountRepository;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserAccount registerUser(UserRegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already taken: " + request.email());
        }

        UserAccount user = UserAccountMapper.fromRegisterRequest(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setGlobalRole(GlobalRole.USER);

        UserAccount saved = userAccountRepository.save(user);
        log.info("Registered user externalId={}, email={}", saved.getExternalId(), saved.getEmail());

        return saved;
    }

    @Override
    public Optional<UserAccount> getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    public UserDto getUserByExternalId(UUID externalId) {
        UserAccount user = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User with externalId " + externalId + " not found"));
        return UserAccountMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto putUser(UserUpdateRequest request, UUID externalId) {
        UserAccount user = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserAccountMapper.applyPut(user, request);

        UserAccount saved = userAccountRepository.save(user);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto patchUser(UserUpdateRequest request, UUID externalId) {
        UserAccount user = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserAccountMapper.applyPatch(user, request);

        UserAccount saved = userAccountRepository.save(user);
        return UserAccountMapper.toDto(saved);
    }
}
