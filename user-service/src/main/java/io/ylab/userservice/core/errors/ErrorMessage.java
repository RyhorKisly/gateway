package io.ylab.userservice.core.errors;

import lombok.Builder;

/**
 * Represents an error message typically used in response to denote specific issues or errors.
 * <p>
 * This record encapsulates information related to an error, such as the affected field and the error message itself.
 */
@Builder
public record ErrorMessage(
        String field,
        String message
) {

}
