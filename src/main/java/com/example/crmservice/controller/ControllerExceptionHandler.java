package com.example.crmservice.controller;

import com.example.crmservice.exception.CustomerNotFoundException;
import com.example.crmservice.exception.UserNotFoundException;
import com.example.crmservice.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested user does not exist")
    @ExceptionHandler(UserNotFoundException.class)
    public void handleUserNotFound() {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested customer does not exist")
    @ExceptionHandler(CustomerNotFoundException.class)
    public void handleCustomerNotFound() {
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The requested username already exists")
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public void handleUserNameAlreadyExists() {
    }
}
