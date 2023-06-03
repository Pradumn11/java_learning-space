package com.tech.learningspace.client;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.client.request.GetAllConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerPreviewResponse;
import com.tech.learningspace.helper.HttpClientHelper;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Repository;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;


@Repository
public class http3EsClient {

   private final HttpClientHelper httpClientHelper;

   public static final String CONTENT_TYPE="Content-Type";
   public static final String APPLICATION_JSON="application/json";
   private final ImmutableMap<String,String>headers;

   public static final String BASEURL="http://localhost:8082";
   private Gson gson=new Gson();
   private final int connectionTimeout;

    public http3EsClient(HttpClientHelper httpClientHelper) {
        this.httpClientHelper = httpClientHelper;
        this.headers = getHeaders();
        this.connectionTimeout = 30;
    }

    private ImmutableMap<String,String>getHeaders(){
        return ImmutableMap.of(CONTENT_TYPE,APPLICATION_JSON);
    }

    public CompletionStage<ConsumerPreviewResponse>getAllConsumer(GetAllConsumerRequest request){
        String url = BASEURL + "/searchAllConsumers";
        HashMap<String, Object> queryParam = new HashMap<>();
        URIBuilder uriBuilder = getUrlWithQueryParams(queryParam, url);

        return httpClientHelper.post(getUrl(uriBuilder), getHeaders(), ConsumerPreviewResponse.class, gson.toJson(request));
    }

    public CompletionStage<Void>addConsumerToEs(AddConsumerRequest request){
        String url = BASEURL + "/addEsConsumer";
        HashMap<String, Object> queryParam = new HashMap<>();
        URIBuilder uriBuilder=getUrlWithQueryParams(queryParam,url);
        return httpClientHelper.post(getUrl(uriBuilder),getHeaders(), Void.class,gson.toJson(request));
    }

    public URIBuilder getUrlWithQueryParams(Map<String,Object>queryParam,String format){
        URIBuilder uriBuilder;
        try {
            uriBuilder=new URIBuilder(format);
            for(Map.Entry<String,Object>entry:queryParam.entrySet()){
            if (queryParam.get(entry.getKey())!=null){
                uriBuilder.addParameter(entry.getKey(),entry.getValue().toString());
            }
            }
            return uriBuilder;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl(URIBuilder uriBuilder){
        try {
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
