package Onol.onol.ExceptionHandler;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    String getErrorCode();
    String getMessage();
    HttpStatus getHttpStatus();
}