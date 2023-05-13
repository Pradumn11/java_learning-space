package com.tech.learningspace.consumers.controller;


import com.tech.learningspace.client.request.GetAllConsumerRequest;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerPreviewResponse;
import com.tech.learningspace.consumers.Response.ConsumerResponse;
import com.tech.learningspace.consumers.service.ConsumerService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/learning-space")
public class ConsumerController {

    private final ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @PostMapping("/addConsumerDetails")
    public CompletionStage<ConsumerResponse>addConsumerDetails(@RequestBody AddConsumerRequest consumerRequest){
         return consumerService.addConsumerDetails(consumerRequest);
    }

    @GetMapping("/getConsumer")
    public CompletionStage<ConsumerResponse>getConsumer(@RequestParam("userId")Long UserId,
                                                        @RequestParam("tenant")Long Tenant){
        return consumerService.getConsumerByUserId(UserId,Tenant);
    }

    @GetMapping("/getAllConsumers")
    public CompletionStage<ConsumerPreviewResponse>getAllConsumer(@RequestBody GetAllConsumerRequest request){
        return consumerService.getAllConsumers(request);
    }
}
