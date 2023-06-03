package com.tech.learningspace.client;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tech.learningspace.Exception.ElasticSearchExceptionHandler;
import com.tech.learningspace.Utils.CompletableFutureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import static com.tech.learningspace.client.http3EsClient.APPLICATION_JSON;
import static com.tech.learningspace.client.http3EsClient.CONTENT_TYPE;

@Component
public class RestTemplateClient {

    private final AsyncRestTemplate asyncRestTemplate;

    private final RestTemplate restTemplate;

    private final  Gson gson;
    @Autowired
    public RestTemplateClient() {
        this.asyncRestTemplate = getAsyncRestTemplate();
        this.restTemplate = getRestTemplate();
        this.gson=new Gson();
    }

    private Map<String, String> getHeaders() {
        return ImmutableMap.of(CONTENT_TYPE, APPLICATION_JSON);
    }

    private AsyncRestTemplate getAsyncRestTemplate() {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        asyncRestTemplate.setMessageConverters(ImmutableList.of(new GsonHttpMessageConverter()));
        asyncRestTemplate.setErrorHandler(new ElasticSearchExceptionHandler());
        return asyncRestTemplate;
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(ImmutableList.of(new GsonHttpMessageConverter()));
        restTemplate.setErrorHandler(new ElasticSearchExceptionHandler());
        return restTemplate;
    }

    public <T> T postEntitySync(String url,String api,Object payload,Class<T>responseClass){
        return restTemplate.postForEntity(String.format("%s/%s",url,api),getHttpEntity(payload),responseClass).getBody();
    }

    public <T>CompletionStage<T>postEntityAsync(String url,String api,Object payload,Class<T>responseClass){

        return CompletableFutureUtils.buildCompletableFuture(
                asyncRestTemplate.postForEntity(String.format("%s/%s",url,api),
                getHttpEntity(payload),responseClass));
    }

    public <T>CompletionStage<T> getEntityAsync(String url,String api,Object payload,Class<T>responseClass){
        JsonElement element=gson.toJsonTree(payload);
        final String[] urlParams={""};

        element.getAsJsonObject()
                .entrySet()
                .forEach(
                        stringJsonElementEntry -> {
                        if (stringJsonElementEntry.getValue().isJsonPrimitive()){
                            urlParams[0]=urlParams[0]+stringJsonElementEntry.getKey()+"="+stringJsonElementEntry.getValue().getAsString()+"&";
                        }
                        }
                );
        return CompletableFutureUtils.buildCompletableFuture(
                asyncRestTemplate.exchange(String.format("%s/%s?%s",url,api,urlParams[0]), HttpMethod.GET
                ,getHttpEntity(payload)
                        ,responseClass));
    }

    public <T> T getEntitySync(String url,String api,Object payload,Class<T>responseClass){
        JsonElement element=gson.toJsonTree(payload);
        final String[] urlParams={""};

        element.getAsJsonObject()
                .entrySet()
                .forEach(
                        stringJsonElementEntry -> {
                            if (stringJsonElementEntry.getValue().isJsonPrimitive()){
                                urlParams[0]=urlParams[0]+stringJsonElementEntry.getKey()+"="+stringJsonElementEntry.getValue().getAsString()+"&";
                            }
                        }
                );
     return restTemplate.getForEntity(String.format("%s/%s?%s",url,api,urlParams[0]),responseClass).getBody();
    }

    public <T>CompletionStage<T> getEntityWithParams(String url,String api,Map<String,Object>param,Class<T>responseClass) {
        final String[] urlParam = {""};

        param.forEach((key,value)->{
            urlParam[0]=urlParam[0]+key+"="+value+"&";
        });
        return CompletableFutureUtils.buildCompletableFuture(
                asyncRestTemplate.getForEntity(String.format("%s/%s?%s",url,api,urlParam[0]),responseClass));
    }
    public <T>HttpEntity<T>getHttpEntity(T request){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request,headers);
    }

}
