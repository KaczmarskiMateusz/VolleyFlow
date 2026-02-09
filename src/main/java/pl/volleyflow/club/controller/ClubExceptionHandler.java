package pl.volleyflow.club.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.volleyflow.authorization.model.ApiErrorResponse;
import pl.volleyflow.club.model.exceptions.ClubPermissionException;
import pl.volleyflow.user.service.exceptions.InvalidCredentialsException;

import static pl.volleyflow.authorization.model.ErrorCode.CLUB_PERMISSION_DENIED;

@ControllerAdvice
public class ClubExceptionHandler {

    @ExceptionHandler(ClubPermissionException.class)
    public ResponseEntity<ApiErrorResponse> handleClubPermissionException(ClubPermissionException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                CLUB_PERMISSION_DENIED, "You do not have permission to perform this action on the club.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

}
