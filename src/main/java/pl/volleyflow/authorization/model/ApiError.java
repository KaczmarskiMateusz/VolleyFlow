package pl.volleyflow.authorization.model;

public record ApiError(
        ErrorCode code,
        String message
) {}
