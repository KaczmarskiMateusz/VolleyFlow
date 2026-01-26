package pl.volleyflow.appconfig;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.volleyflow.authorization.model.ApiErrorResponse;
import pl.volleyflow.user.service.EmailAlreadyExistsException;

import static pl.volleyflow.authorization.model.ErrorCode.AUTH_INVALID_CREDENTIALS;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(EmailAlreadyExistsException ex) {
        ApiErrorResponse body = new ApiErrorResponse(AUTH_INVALID_CREDENTIALS, "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

}