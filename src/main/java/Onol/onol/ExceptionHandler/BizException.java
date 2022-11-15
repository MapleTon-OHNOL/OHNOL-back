package Onol.onol.ExceptionHandler;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException{
    private final BaseExceptionType baseExceptionType;

    public BizException(BaseExceptionType baseExceptionType){
        super(baseExceptionType.getMessage());
        this.baseExceptionType = baseExceptionType;
    }

}