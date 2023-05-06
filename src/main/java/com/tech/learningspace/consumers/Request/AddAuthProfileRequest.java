package com.tech.learningspace.consumers.Request;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAuthProfileRequest {
    private String mobileNumber;
    private String email;
    private Long tenantId;
    private String password;
    private String role;
}
