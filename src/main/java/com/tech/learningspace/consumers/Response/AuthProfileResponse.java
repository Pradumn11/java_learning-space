package com.tech.learningspace.consumers.Response;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthProfileResponse {

    private Long userId;
    private String mobileNumber;
    private String email;
    private String tenantId;
    private String status;

}
