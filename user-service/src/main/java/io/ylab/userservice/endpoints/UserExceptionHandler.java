package io.ylab.userservice.endpoints;

import io.ylab.userservice.core.enums.ErrorType;
import io.ylab.userservice.core.errors.ErrorMessage;
import io.ylab.userservice.core.errors.ErrorResponse;
import io.ylab.userservice.core.errors.StructuredErrorResponse;
import io.ylab.userservice.core.exceptions.RoleNotExistException;
import io.ylab.userservice.core.exceptions.UserServiceException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for user-related endpoints.
 */
@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
    private static final String SERVER_ERROR = "Internal server Error. Please, contact support!";

    /**
     * Handles invalid arguments passed in JSON format according to DTO validation.
     * @param ex The exception to MethodArgumentNotValidException type
     * @return ResponseEntity containing a structured error response for bad requests
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
    })
    public ResponseEntity<StructuredErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors();
        StructuredErrorResponse response = StructuredErrorResponse.builder()
                .errors(new ArrayList<>())
                .logref(ErrorType.STRUCTURED_ERROR)
                .build();

        for (FieldError error : errors) {
            response.errors().add( new ErrorMessage(error.getField(), error.getDefaultMessage()));
        }
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * If something goes wrong beyond the user's control
     * @param ex The exception to RoleNotExistException type
     * @return ResponseEntity containing an error response for internal server errors
     */
    @ExceptionHandler({
            RoleNotExistException.class
    })
    public ResponseEntity<ErrorResponse> handleProviderProblems(UserServiceException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .logref(ErrorType.ERROR)
                .message(SERVER_ERROR)
                .build();
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions for not found resources and unreadable HTTP messages.
     * @param ex The exception to NotFoundException or HttpMessageNotReadableException type
     * @return ResponseEntity containing an error response for bad requests or not found resources
     */
    @ExceptionHandler({
            NotFoundException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleDedRequest(RuntimeException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .logref(ErrorType.ERROR)
                .message(ex.getMessage())
                .build();
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles WebApplicationExceptions and returns a CONFLICT response.
     * @param ex The exception to WebApplicationException type
     * @return ResponseEntity containing an error response for conflict or bad requests
     */
    @ExceptionHandler({
            WebApplicationException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .logref(ErrorType.ERROR)
                .message(ex.getMessage())
                .build();
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

        /**
         * Handles general exceptions and returns an Internal Server Error response.
         * @param ex The exception to handle
         * @return ResponseEntity with an Internal Server Error status
         */
    @ExceptionHandler({
            Exception.class
    })
    public ResponseEntity<ErrorResponse> handleInnerError(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .logref(ErrorType.ERROR)
                .message(SERVER_ERROR)
                .build();

        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}