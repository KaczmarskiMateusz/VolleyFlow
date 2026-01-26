package pl.volleyflow.authorization.model;

public record ApiErrorResponse(
        ErrorCode code,
        String message
) {
}
