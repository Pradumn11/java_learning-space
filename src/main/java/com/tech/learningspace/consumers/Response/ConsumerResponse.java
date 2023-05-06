package com.tech.learningspace.consumers.Response;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerResponse {
    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String address;
    private String mobileNumber;
    private String email;
    private String tenantId;
    private String userId;
    private String status;
}
