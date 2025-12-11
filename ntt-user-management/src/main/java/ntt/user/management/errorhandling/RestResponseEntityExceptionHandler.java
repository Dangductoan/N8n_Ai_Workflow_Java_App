package ntt.user.management.errorhandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        String message = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
        return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleRuntimeException(Exception ex, WebRequest request) {
        String message = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
        CommonConfigurationError apiError = new CommonConfigurationError(HttpStatus.INTERNAL_SERVER_ERROR, message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}