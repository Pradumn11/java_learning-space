package com.tech.learningspace.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.Exception.LearningSpaceWarningException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@Component
public class HttpClientHelper {

    private OkHttpClient okHttpClient;

    private Gson gson;

    private MediaType MEDIA_TYPE_JSON=MediaType.parse("application/json; charset=utf-8");

    @Autowired
    public HttpClientHelper(){
        this.okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        gson=new GsonBuilder().create();
    }

    public <T>CompletionStage<T>get(String url, Map<String,String>headers,Class<T> returnType){
        Request request= new Request.Builder()
                .url(HttpUrl.parse(url))
                .headers(Headers.of(headers))
                .build();
        CompletableFuture<T> completableFuture= new CompletableFuture<T>();
        okHttpClient.newCall(request)
                .enqueue(getCallBack(url,"NA",completableFuture,returnType));
        return completableFuture;
    }

    public <T> CompletionStage<T>post(String url,Map<String,String>headers,Class<T>returnType,String body){
        RequestBody createBody=RequestBody.create(MEDIA_TYPE_JSON,body);
        Request request=new Request.Builder()
                .url(HttpUrl.parse(url))
                .headers(Headers.of(headers))
                .post(createBody)
                .build();
        CompletableFuture<T>completableFuture=new CompletableFuture<>();
        okHttpClient.newCall(request)
                .enqueue(getCallBack(url,body,completableFuture,returnType));
        return completableFuture;
    }

    public <T>CompletionStage<T>put(String url,Map<String,String>headers,Class<T>returnType,String body){
        RequestBody createBody=RequestBody.create(MEDIA_TYPE_JSON,body);
        Request request=new Request.Builder()
                .url(HttpUrl.parse(url))
                .headers(Headers.of(headers))
                .put(createBody)
                .build();
        CompletableFuture<T>completableFuture=new CompletableFuture<>();
        okHttpClient.newCall(request)
                .enqueue(getCallBack(url,body,completableFuture,returnType));
        return completableFuture;
    }

    private <T> Callback getCallBack(String url, String body, CompletableFuture<T> completableFuture,Class<T> returnType){

        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                    String output=response.body().string();

                    if (String.class==returnType){
                        T value=(T)output;
                        completableFuture.complete(value);
                    }else {
                        T value=gson.fromJson(output,returnType);
                        completableFuture.complete(value);
                    }
                }else {
                    if (Optional.ofNullable(response.body()).isPresent() && response.body().contentLength()>0){
                        String errorBody=new String(response.body().bytes());
                        completableFuture.completeExceptionally(new LearningSpaceWarningException(response.message(),response.code(),errorBody));
                    }else {
                        completableFuture.completeExceptionally(new LearningSpaceWarningException(response.message(),response.code(),"SERVICE ERROR"));
                    }
                }

            }
        };
    }
}
