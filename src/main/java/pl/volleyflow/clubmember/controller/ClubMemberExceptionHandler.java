package pl.volleyflow.clubmember.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.volleyflow.authorization.model.ApiErrorResponse;
import pl.volleyflow.club.model.exceptions.ClubNotFoundException;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberAlreadyExistsException;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberForbiddenException;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberValidationException;

import static pl.volleyflow.authorization.model.ErrorCode.CLUB_MEMBER_ALREADY_EXISTS;
import static pl.volleyflow.authorization.model.ErrorCode.CLUB_MEMBER_FORBIDDEN;
import static pl.volleyflow.authorization.model.ErrorCode.CLUB_MEMBER_INVALID_REQUEST;
import static pl.volleyflow.authorization.model.ErrorCode.CLUB_NOT_FOUND;

@RestControllerAdvice(assignableTypes = ClubMemberController.class)
public class ClubMemberExceptionHandler {

    @ExceptionHandler(ClubMemberValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleClubMemberValidationException(ClubMemberValidationException ex) {
        ApiErrorResponse body = new ApiErrorResponse(CLUB_MEMBER_INVALID_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ClubMemberAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleClubMemberAlreadyExistsException(ClubMemberAlreadyExistsException ex) {
        ApiErrorResponse body = new ApiErrorResponse(CLUB_MEMBER_ALREADY_EXISTS, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ClubMemberForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleClubMemberForbiddenException(ClubMemberForbiddenException ex) {
        ApiErrorResponse body = new ApiErrorResponse(CLUB_MEMBER_FORBIDDEN, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ClubNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleClubNotFoundException(ClubNotFoundException ex) {
        ApiErrorResponse body = new ApiErrorResponse(CLUB_NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}
