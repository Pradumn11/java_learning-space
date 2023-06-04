package com.tech.learningspace.consumers.service;

import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.Utils.CompletableFutureUtils;
import com.tech.learningspace.client.RestTemplateClient;
import com.tech.learningspace.client.RetrofitEsClient;
import com.tech.learningspace.client.http3EsClient;
import com.tech.learningspace.client.request.GetAllConsumerRequest;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerPreviewResponse;
import com.tech.learningspace.consumers.Response.ConsumerResponse;
import com.tech.learningspace.consumers.dao.AuthProfileDao;
import com.tech.learningspace.consumers.dao.ConsumersDao;
import com.tech.learningspace.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletionStage;

import static com.tech.learningspace.Utils.CompletableFutureUtils.getCompletableFuture;
import static com.tech.learningspace.client.http3EsClient.BASEURL;

@Service
public class ConsumerService {

    private final ConsumersDao consumersDao;
    private final AuthProfileDao authProfileDao;

    private final RestTemplateClient restTemplateClient;

    private final http3EsClient http3EsClient;

    private final RetrofitEsClient retrofitEsClient;

    @Autowired
    public ConsumerService(ConsumersDao consumersDao, AuthProfileDao authProfileDao, RestTemplateClient restTemplateClient, http3EsClient http3EsClient, RetrofitEsClient retrofitEsClient) {
        this.consumersDao = consumersDao;
        this.authProfileDao = authProfileDao;
        this.restTemplateClient = restTemplateClient;
        this.http3EsClient = http3EsClient;
        this.retrofitEsClient = retrofitEsClient;
    }

    public CompletionStage<ConsumerResponse> addConsumerDetails(AddConsumerRequest addConsumerDetails) {
        return authProfileDao.getProfileByMobileOrEmail(addConsumerDetails.getMobileNumber(), addConsumerDetails.getEmail(), addConsumerDetails.getTenantId())
                .thenApply(authProfile -> authProfile.orElseThrow(() -> new LearningSpaceException("Mobile/Email not Registered", ErrorCode.NOT_EXISTS, HttpStatus.BAD_REQUEST)))
                .thenCompose(authProfileResponse -> {
                    System.out.println(authProfileResponse);
                    addConsumerDetails.setUserId(authProfileResponse.getUserId().toString());
                    return consumersDao.addConsumer(addConsumerDetails, authProfileResponse.getUserId())
                            .thenApply(__->
//                                    http3EsClient.addConsumerToEs(addConsumerDetails)
                                    retrofitEsClient.addEsConsumer(addConsumerDetails)
//                                   restTemplateClient.postEntityAsync(BASEURL,"addEsConsumer",addConsumerDetails,Void.class)
                            )
                            .thenCompose(__ -> consumersDao.getConsumerByUserId(authProfileResponse.getUserId(), addConsumerDetails.getTenantId()));
                })
                .thenApply(consumer -> consumer.orElseThrow(() -> new LearningSpaceException("Error Creating Consumer", ErrorCode.DB_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)))
                .exceptionally(t -> {
                    t = CompletableFutureUtils.unwrapCompletionStateException(t);
                    if (t instanceof LearningSpaceException) {
                        throw new LearningSpaceException(t.getMessage(), ((LearningSpaceException) t).getErrorCode(), ((LearningSpaceException) t).getStatus());
                    }
                    throw new LearningSpaceException(t.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    public CompletionStage<ConsumerResponse> getConsumerByUserId(Long userID, Long tenantId) {
        return consumersDao.getConsumerByUserId(userID, tenantId)
                .thenApply(consumerResponse ->
                        consumerResponse.orElseThrow(() -> new LearningSpaceException("Mobile/Email not Registered", ErrorCode.NOT_EXISTS, HttpStatus.BAD_REQUEST)))
                .exceptionally(t -> {
                    t = CompletableFutureUtils.unwrapCompletionStateException(t);
                    if (t instanceof LearningSpaceException) {
                        throw new LearningSpaceException(t.getMessage(), ((LearningSpaceException) t).getErrorCode(), ((LearningSpaceException) t).getStatus());
                    }
                    throw new LearningSpaceException(t.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    public CompletionStage<ConsumerPreviewResponse> getAllConsumers(GetAllConsumerRequest request) {
//        return http3EsClient.getAllConsumer(request);
        return retrofitEsClient.searchAllMerchantsAsync(request);
        // return restTemplateClient.postEntityAsync(BASEURL,"/searchAllConsumers",request,ConsumerPreviewResponse.class);
    }
}
