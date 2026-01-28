package pl.volleyflow.authorization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.volleyflow.authorization.model.JwtResponse;
import pl.volleyflow.authorization.model.UserLoginRequest;
import pl.volleyflow.authorization.model.UserRegisterRequest;
import pl.volleyflow.authorization.service.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(authService.register(request).token()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(authService.login(request).token()));
    }

}
