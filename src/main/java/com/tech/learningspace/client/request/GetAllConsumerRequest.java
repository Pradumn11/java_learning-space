package com.tech.learningspace.client.request;


import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllConsumerRequest {

    Map<String,Object>fields;
    Integer pageNumber;
    Integer pageSize;
    Long tenantId;
}
