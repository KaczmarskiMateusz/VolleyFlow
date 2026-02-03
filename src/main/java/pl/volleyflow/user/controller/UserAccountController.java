package pl.volleyflow.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.volleyflow.authorization.model.UserPrincipal;
import pl.volleyflow.authorization.model.UserUpdateRequest;
import pl.volleyflow.user.model.UserDto;
import pl.volleyflow.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/app/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserAccount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserByExternalId(principal.externalId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> putMe(@Valid @RequestBody UserUpdateRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.putUser(request, principal.externalId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> patchMe(@RequestBody UserUpdateRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.patchUser(request, principal.externalId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteUser(principal.externalId());
        return ResponseEntity.noContent().build();
    }


}
