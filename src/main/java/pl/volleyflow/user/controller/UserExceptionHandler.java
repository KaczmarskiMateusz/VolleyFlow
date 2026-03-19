package pl.volleyflow.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.volleyflow.authorization.model.ApiErrorResponse;
import pl.volleyflow.user.service.exceptions.EmailAlreadyExistsException;
import pl.volleyflow.user.service.exceptions.UserAlreadyExistsException;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

import static pl.volleyflow.authorization.model.ErrorCode.EMAIL_ALREADY_EXISTS;
import static pl.volleyflow.authorization.model.ErrorCode.USER_NOT_FOUND;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiErrorResponse body = new ApiErrorResponse(EMAIL_ALREADY_EXISTS, "Email already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ApiErrorResponse body = new ApiErrorResponse(EMAIL_ALREADY_EXISTS, "Email already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ApiErrorResponse body = new ApiErrorResponse(USER_NOT_FOUND, "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}
