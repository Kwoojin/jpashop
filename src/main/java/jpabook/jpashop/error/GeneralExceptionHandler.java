package jpabook.jpashop.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleInvalidFormatException(InvalidFormatException e) {
        return new ErrorResult(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
    }
}
