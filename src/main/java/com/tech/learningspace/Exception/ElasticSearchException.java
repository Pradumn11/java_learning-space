package com.tech.learningspace.Exception;



import com.tech.learningspace.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ElasticSearchException extends  RuntimeException{

    String errorMessage;
    ErrorCode errorCode;
    HttpStatus status;

    public ElasticSearchException(String errorMessage, ErrorCode errorCode, HttpStatus status) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.status=status;

    }
    public ElasticSearchException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
