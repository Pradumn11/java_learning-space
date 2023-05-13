package com.tech.learningspace.Exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableList;
import com.tech.learningspace.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class LearningSpaceExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    public ResponseEntity<Object>handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                              HttpStatus status, WebRequest request){

        ApiError apiError= ApiError.builder()
                .errors(ex.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList()))
                .errorCode("ERROR_01")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object>handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,HttpHeaders httpHeaders,HttpStatus status,WebRequest request
    ){
        List<String>errors=new ArrayList<>();
        if(ex.getCause() instanceof InvalidFormatException){
            InvalidFormatException invalidFormatException=(InvalidFormatException) ex.getCause();

        if (invalidFormatException.getTargetType().isEnum()){
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("Invalid value for key: ");
            stringBuilder.append(invalidFormatException.getPathReference(),invalidFormatException.getPathReference().indexOf("\"")+1,
                    invalidFormatException.getPathReference().lastIndexOf("\""));
            errors.add(stringBuilder.toString());
        }else {
            errors.addAll(ImmutableList.of(Objects.requireNonNull(ex.getMostSpecificCause().getMessage())));
        }
        }else {
            errors.addAll(ImmutableList.of(Objects.requireNonNull(ex.getMostSpecificCause().getMessage())));
        }
        ApiError apiError= ApiError.builder()
                .errors(errors)
                .errorCode("InvalidData")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {LearningSpaceException.class})
    public ResponseEntity<Object>handleAuthException(LearningSpaceException ex){

        ApiError apiError= ApiError.builder()
                .errors(ImmutableList.of(ex.getMessage()))
                .errorCode(ex.errorCode.getErrorCode())
                .status(ex.status.value())
                .build();
        return new ResponseEntity<>(apiError,ex.status);
    }

    @ExceptionHandler(value = {LearningSpaceWarningException.class})
    public ResponseEntity<Object>handleWarningException(LearningSpaceWarningException ex){

        ApiError apiError= ApiError.builder()
                .errors(ImmutableList.of(ex.getMessage()))
                .errorCode(ex.getErrorResponse())
                .status(ex.getStatus())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.resolve(ex.getStatus()));
    }

}
