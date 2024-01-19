package io.ylab.userservice.core.errors;

import io.ylab.userservice.core.enums.ErrorType;
import lombok.Builder;

/**
 * Represents an error response commonly used to convey errors or issues in the system.
 * <p>
 * This record encapsulates an error type reference and a descriptive message about the error encountered.
 */
@Builder
public record ErrorResponse(
        ErrorType logref,
        String message
) {
}
