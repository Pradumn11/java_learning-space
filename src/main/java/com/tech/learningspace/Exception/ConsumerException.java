package com.tech.learningspace.Exception;

import org.springframework.http.HttpStatus;

public class ConsumerException extends RuntimeException{

   private String message;
   private HttpStatus status;

    public ConsumerException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
