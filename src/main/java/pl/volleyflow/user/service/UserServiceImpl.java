package pl.volleyflow.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.model.UserUpdateRequest;
import pl.volleyflow.clubmember.model.MemberProfile;
import pl.volleyflow.user.model.GlobalRole;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserAccountMapper;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.repository.UserAccountRepository;
import pl.volleyflow.user.service.exceptions.UserAlreadyExistsException;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

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
        String email = request.email() == null ? null : request.email().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        if (userAccountRepository.existsByLoginEmail(email)) {
            throw new UserAlreadyExistsException("Email already taken: " + email);
        }

        UserAccount user = UserAccountMapper.fromRegisterRequest(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setGlobalRole(GlobalRole.USER);

        MemberProfile profile = MemberProfile.builder()
                .contactEmail(email)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .phoneNumber(request.phoneNumber())
                .displayName(request.displayName())
                .avatarUrl(request.avatar())
                .build();

        user.setMemberProfile(profile);
        profile.setUserAccount(user);

        UserAccount saved = userAccountRepository.save(user);
        log.info("Registered user externalId={}, email={}", saved.getExternalId(), saved.getLoginEmail());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserByEmail(String email) {
        String normalized = email == null ? null : email.trim().toLowerCase();
        if (normalized == null || normalized.isBlank()) return Optional.empty();
        return userAccountRepository.findByLoginEmail(normalized);
    }

    @Override
    @Transactional(readOnly = true)
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
        log.info("Put user successful externalId={}, email={}", externalId, saved.getLoginEmail());
        return UserAccountMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto patchUser(UserUpdateRequest request, UUID externalId) {
        UserAccount user = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserAccountMapper.applyPatch(user, request);

        UserAccount saved = userAccountRepository.save(user);
        log.info("Patch user successful externalId={}, email={}", saved.getExternalId(), saved.getLoginEmail());
        return UserAccountMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID externalId) {
        UserAccount user = userAccountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userAccountRepository.delete(user);
        log.info("Deleted user externalId={}, email={}", externalId, user.getLoginEmail());
    }

}
