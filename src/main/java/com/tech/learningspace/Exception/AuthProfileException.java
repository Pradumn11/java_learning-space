package com.tech.learningspace.Exception;


import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class AuthProfileException extends RuntimeException{
    String error;
    String message;
    HttpStatus errorCode;

    public AuthProfileException(String error, String message, HttpStatus errorCode) {
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
    }
}
