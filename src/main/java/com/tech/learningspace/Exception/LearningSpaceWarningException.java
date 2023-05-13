package com.tech.learningspace.Exception;

import lombok.Getter;

@Getter
public class LearningSpaceWarningException extends RuntimeException {
    private String errorMsg;
    private String errorResponse;
    private int status;


    public LearningSpaceWarningException(String message,int status, String errorResponse) {
        super(message);
        this.errorMsg = message;
        this.errorResponse = errorResponse;
        this.status=status;
    }


}
