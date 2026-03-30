package pl.volleyflow.appconfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.volleyflow.authorization.model.ApiErrorResponse;

import static pl.volleyflow.authorization.model.ErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION;

public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                METHOD_ARGUMENT_NOT_VALID_EXCEPTION, "Method argument not valid exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
