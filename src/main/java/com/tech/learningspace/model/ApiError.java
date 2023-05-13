package com.tech.learningspace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(builderClassName = "Builder")
public class ApiError {

    private int status;
    private List<String>errors;
    private String errorCode;
}
