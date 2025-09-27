package picklunch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import picklunch.exception.PickLunchException;
import picklunch.model.ErrorResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        // for angular router
        return "forward:/index.html";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PickLunchException.class)
    public @ResponseBody ErrorResponse handlePickLunchException(PickLunchException exception) {
        log.warn("Handling exception, message={}", exception.getMessage(), exception);
        return ErrorResponse.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .build();
    }

}
