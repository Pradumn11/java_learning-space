package com.tech.learningspace.enums;

public enum ErrorCode {

    INVALID_DATA("ERROR_001"),
    DUPLICATE_DATA("ERROR_002"),
    DB_ERROR("ERROR_003"),
    NOT_EXISTS("ERROR_004"),
    INTERNAL_SERVER_ERROR("ERROR_005"),

    CONNECTION_ERROR("ERROR_006"),
    UNAUTHORIZED("ERROR_401");

    private String errorCode;

    ErrorCode(String errorCode){
        this.errorCode=errorCode;
    }
    public String getErrorCode(){
        return this.errorCode;
    }
}
