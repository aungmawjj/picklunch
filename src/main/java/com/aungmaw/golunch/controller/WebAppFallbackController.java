package com.aungmaw.golunch.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class WebAppFallbackController {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        return "forward:/index.html";
    }

}
