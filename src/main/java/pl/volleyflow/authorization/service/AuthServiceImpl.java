package pl.volleyflow.authorization.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.volleyflow.appconfig.JwtService;
import pl.volleyflow.authorization.model.AuthResponse;
import pl.volleyflow.authorization.model.UserLoginRequest;
import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.model.UserAccountMapper;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.service.InvalidCredentialsException;
import pl.volleyflow.user.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(UserRegisterRequest request) {
        UserAccount userAccount = userService.registerUser(request);
        LOGGER.info("User account {} registered successfully.", userAccount.getEmail());

        String token = jwtService.generateToken(userAccount);
        UserDto userDto = UserAccountMapper.toDto(userAccount);

        return AuthResponse.success(userDto, token);
    }

    @Override
    public AuthResponse login(UserLoginRequest request) {
        String email = request.email().trim().toLowerCase();

        UserAccount user = userService.getUserByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!validPassword(request.password(), user.getPasswordHash())) {
            LOGGER.warn("Login failed for {}", email);
            throw new InvalidCredentialsException();
        }

        LOGGER.info("User {} logged in successfully.", email);

        String token = jwtService.generateToken(user);
        UserDto userDto = UserAccountMapper.toDto(user);
        return AuthResponse.success(userDto, token);
    }

    private boolean validPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}
