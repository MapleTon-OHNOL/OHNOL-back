package Onol.onol.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;

    static ErrorResult create(BaseExceptionType baseExceptionType){
        return new ErrorResult(baseExceptionType.getErrorCode(),baseExceptionType.getMessage());
    }
}