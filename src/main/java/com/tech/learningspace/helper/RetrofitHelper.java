package com.tech.learningspace.helper;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitHelper {


    public static <T>T getRetrofit(String baseurl,Class<T> className){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(className);
    }

    public static <T>T getRetrofit (String baseUrl, Class<T>className, Converter.Factory factory){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(factory)
                .build();
        return retrofit.create(className);
    }

    public static <T>T execute(Call<T>call){
        try {
            return handleResponse(call.execute());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static <T>T handleResponse(Response <T>response)throws IOException {
        if (response.isSuccessful()){
            return response.body();
        }
        else {
            throw new ResponseStatusException(HttpStatus.valueOf(response.code()),response.errorBody()!=null?response.errorBody().toString():response.message());
        }
    }



}
