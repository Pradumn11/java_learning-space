package com.tech.learningspace.Exception;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ElasticSearchExceptionHandler implements ResponseErrorHandler {

    private DefaultResponseErrorHandler defaultResponseErrorHandle= new DefaultResponseErrorHandler();

    private final Gson gson=new Gson();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return defaultResponseErrorHandle.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
     ElasticSearchException elasticSearchException=getEsException(response);
     if (elasticSearchException!=null){
         throw elasticSearchException;
     }else {
         defaultResponseErrorHandle.handleError(response);
     }
    }

    private ElasticSearchException getEsException(ClientHttpResponse response)throws IOException{
        String jsonString= IOUtils.toString(response.getBody());
        try {
            return gson.fromJson(jsonString,ElasticSearchException.class);
        }catch(Exception e){
            return null;
        }
    }
}
