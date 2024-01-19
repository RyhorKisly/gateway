package io.ylab.userservice.core.errors;

import io.ylab.userservice.core.enums.ErrorType;
import lombok.Builder;

import java.util.List;

/**
 * Represents a structured error response used to provide detailed error information.
 * <p>
 * This record encapsulates an error type reference along with a list of error messages for detailed error reporting.
 */
@Builder
public record StructuredErrorResponse(
        ErrorType logref,
        List<ErrorMessage> errors
) {
}
