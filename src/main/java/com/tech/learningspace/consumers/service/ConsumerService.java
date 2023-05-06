package com.tech.learningspace.consumers.service;

import com.tech.learningspace.Exception.AuthProfileException;
import com.tech.learningspace.Exception.ConsumerException;
import com.tech.learningspace.Utils.CompletableFutueUtils;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerResponse;
import com.tech.learningspace.consumers.dao.AuthProfileDao;
import com.tech.learningspace.consumers.dao.ConsumersDao;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletionStage;

public class ConsumerService {

    private ConsumersDao consumersDao;
    private AuthProfileDao authProfileDao;

    public CompletionStage<ConsumerResponse>addConsumerDetails(AddConsumerRequest addConsumerDetails){
        return authProfileDao.getProfileByMobileOrEmail(addConsumerDetails.getMobileNumber(),addConsumerDetails.getEmail(),addConsumerDetails.getTenantId())
                .thenApply(authProfile->authProfile.orElseThrow(()->new AuthProfileException("Error_01","Mobile/Email not Registered", HttpStatus.BAD_REQUEST)))
                .thenCompose(authProfileResponse -> consumersDao.addConsumer(addConsumerDetails,authProfileResponse.getUserId())
                            .thenCompose(__->consumersDao.getConsumerByUserId(authProfileResponse.getUserId(),addConsumerDetails.getTenantId())))
                .thenApply(consumer-> consumer.orElseThrow(()->new AuthProfileException("Error_01","Error Creating Consumer", HttpStatus.INTERNAL_SERVER_ERROR)))
                .exceptionally(t-> {
                  t= CompletableFutueUtils.unwrapCompletionStateException(t);
                  throw new ConsumerException(t.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    public CompletionStage<ConsumerResponse>getConsumerByUserId(Long userID,Long tenantId){
        return consumersDao.getConsumerByUserId(userID,tenantId)
                .thenApply(consumerResponse ->
                    consumerResponse.orElseThrow(()->new ConsumerException("Mobile/Email not Registered", HttpStatus.BAD_REQUEST)))
                .exceptionally(t->{
                    t=CompletableFutueUtils.unwrapCompletionStateException(t);
                    throw new ConsumerException(t.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }


}
