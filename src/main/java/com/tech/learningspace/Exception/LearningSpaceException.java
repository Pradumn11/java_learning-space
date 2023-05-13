package com.tech.learningspace.Exception;


import com.tech.learningspace.enums.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;



@NoArgsConstructor
@Getter
@Builder
public class LearningSpaceException extends RuntimeException{
    String message;
    ErrorCode errorCode;
    HttpStatus status;

    public LearningSpaceException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }
}
