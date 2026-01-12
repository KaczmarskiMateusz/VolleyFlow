package pl.volleyflow.authorization.service;

import pl.volleyflow.authorization.model.AuthResponse;
import pl.volleyflow.authorization.model.UserLoginRequest;
import pl.volleyflow.authorization.model.UserRegisterRequest;

public interface AuthService {

    AuthResponse register(UserRegisterRequest request);

    AuthResponse login(UserLoginRequest request);

}
