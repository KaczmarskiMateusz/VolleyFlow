package pl.volleyflow.authorization.model;

import java.util.List;

public record ApiErrorResponse(
        ErrorCode errorCode,
        String message
) {

}
