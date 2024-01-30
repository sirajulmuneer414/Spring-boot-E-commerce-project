package com.sirajul.lenscraft.exception.handling;

import com.sirajul.lenscraft.exception.BlockedUserFoundException;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomExceptionHandler implements ErrorController {
    @ExceptionHandler(BlockedUserFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(BlockedUserFoundException ex){
        return "redirect:/login?userBlocked=true";

    }
    @ExceptionHandler(InvalidOtpException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String technicalException(InvalidOtpException ex){
        return "redirect:/verification?invalidOtp=true";
    }

}
