package com.tech.learningspace.client;

import com.tech.learningspace.client.request.GetAllConsumerRequest;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerPreviewResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import java.util.Map;

public interface RetroEsClientInterface {


    @POST("/searchAllConsumers")
    Call<ConsumerPreviewResponse> searchAllConsumers(@Body GetAllConsumerRequest getAllConsumerRequest,
                                                     @HeaderMap Map<String, String> headers
    );


    @POST("/addEsConsumer")
    Call<Void> addEsConsumer(@Body AddConsumerRequest request,
                                                @HeaderMap Map<String, String> headers
    );

}
