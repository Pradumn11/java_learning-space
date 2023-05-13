package com.tech.learningspace.consumers.Response;

import com.tech.learningspace.consumers.Response.ConsumerResponse;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerPreviewResponse {

    List<ConsumerResponse>consumers;
    Long totalCount;
}
