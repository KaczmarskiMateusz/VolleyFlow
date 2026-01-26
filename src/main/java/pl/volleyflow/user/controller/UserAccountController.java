package pl.volleyflow.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.volleyflow.authorization.model.UserPrincipal;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.service.UserService;

@RestController
@RequestMapping("/app/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @GetMapping("/me")
    ResponseEntity<UserDto> getUserAccount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserByExternalId(principal.externalId()));
    }



}
