package pl.volleyflow.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.volleyflow.authorization.model.ApiErrorResponse;
import pl.volleyflow.user.service.exceptions.EmailAlreadyExistsException;

import static pl.volleyflow.authorization.model.ErrorCode.EMAIL_ALREADY_EXISTS;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiErrorResponse body = new ApiErrorResponse(EMAIL_ALREADY_EXISTS, "Email already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}
