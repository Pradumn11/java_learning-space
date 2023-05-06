package com.tech.learningspace.consumers.Request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddConsumerRequest {
    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String address;
    private String mobileNumber;
    private String email;
    private Long tenantId;
}
