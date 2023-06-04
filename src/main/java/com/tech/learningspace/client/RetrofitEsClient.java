package com.tech.learningspace.client;


import com.google.common.collect.ImmutableMap;
import com.tech.learningspace.client.request.GetAllConsumerRequest;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerPreviewResponse;
import com.tech.learningspace.helper.RetrofitHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import static com.tech.learningspace.Utils.CompletableFutureUtils.getCompletableFuture;

@Component
public class RetrofitEsClient {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    public static final String BASEURL="http://localhost:8082";

    private final RetroEsClientInterface retroEsClientInterface;


    public RetrofitEsClient() {
        this.retroEsClientInterface = RetrofitHelper.getRetrofit(BASEURL, RetroEsClientInterface.class);
    }

    public ConsumerPreviewResponse searchAllMerchantsSync(GetAllConsumerRequest getAllConsumerRequest){
        return RetrofitHelper.execute(retroEsClientInterface.searchAllConsumers(getAllConsumerRequest,getHeaders()));
    }

    public CompletionStage<ConsumerPreviewResponse>searchAllMerchantsAsync(GetAllConsumerRequest getAllConsumerRequest){
        return getCompletableFuture(retroEsClientInterface.searchAllConsumers(getAllConsumerRequest,getHeaders()));
    }

    public Void addEsConsumer(AddConsumerRequest request){
        return RetrofitHelper.execute(retroEsClientInterface.addEsConsumer(request,getHeaders()));
    }


    public Map<String,String>getHeaders(){
        Map<String,String>headers= ImmutableMap.of(CONTENT_TYPE,APPLICATION_JSON);
       return headers;
    }
}
