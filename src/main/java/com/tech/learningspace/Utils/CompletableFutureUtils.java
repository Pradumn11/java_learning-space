package com.tech.learningspace.Utils;

import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CompletableFutureUtils {
    public CompletableFutureUtils() {
    }

    public static <T> CompletionStage<T> unwrapCompletionStateException(CompletionStage<T> oldcompletionStage) {
        CompletableFuture<T> newcompletableFuture = new CompletableFuture<>();
        oldcompletionStage.whenComplete((v, throwable) -> {
            if (throwable == null) {
                newcompletableFuture.complete(v);
            } else {
                newcompletableFuture.completeExceptionally(throwable);
            }
        });
        return newcompletableFuture;
    }

    public static Throwable unwrapCompletionStateException(Throwable throwable) {
        int maxDepth = 30;
        if (maxDepth <= 0) {
            maxDepth = 2;
        }

        while (isCompletionException(throwable) && maxDepth-- > 0) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    public static boolean isCompletionException(Throwable ex) {
        return null != ex && CompletionException.class.isAssignableFrom(ex.getClass());
    }

    public static <T> DeferredResult<T> getDeferredResult(CompletableFuture<T> future) {
        DeferredResult<T> deferredResult = new DeferredResult<>(600000L);
        future.thenAccept(deferredResult::setResult)
                .whenComplete(thenOnException(deferredResult::setErrorResult));
        return deferredResult;
    }

    public static BiConsumer<Object, Throwable> thenOnException(Consumer<Throwable> throwableConsumer) {
        return (o, throwable) -> {
            if (throwable != null) {
                throwableConsumer.accept(getUnwrappedException(throwable));
            }
        };
    }

    public static Throwable getUnwrappedException(Throwable throwable) {
        while (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    public static <T>CompletableFuture<T> buildCompletableFuture(final ListenableFuture<ResponseEntity<T>> listenableFuture){
        //create an instance of completableFuture
        CompletableFuture<T>completableFuture=new CompletableFuture<>(){
            public  boolean cancel(boolean mayInterruptRunning){
                boolean result=listenableFuture.cancel(mayInterruptRunning);
                super.cancel(mayInterruptRunning);
                return result;
            }
        };

        listenableFuture.addCallback(new ListenableFutureCallback<ResponseEntity<T>>() {
            @Override
            public void onFailure(Throwable ex) {
                completableFuture.completeExceptionally(ex);
            }

            @Override
            public void onSuccess(ResponseEntity<T> result) {
               completableFuture.complete(result.getBody());
            }
        });
        return completableFuture;
    }

    public static  <T>CompletableFuture<T> getCompletableFuture(Call call){
    CompletableFuture<T> cf=new CompletableFuture<>();
    call.enqueue(convertRetrofitToCallback(cf));
    return cf;
    }

    private static <T>Callback<T>convertRetrofitToCallback(final CompletableFuture<T> cf){

        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                int statusCode=response.code();
                if (response.isSuccessful()){
                    cf.complete(response.body());
                }else {
                    try {
                        String errorBody=response.errorBody().toString();
                        doErrorHandling(statusCode,cf,errorBody);
                    }catch (Exception ex){
                        cf.completeExceptionally(new LearningSpaceException("Failed to Read Response",ErrorCode.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR));
                    }

                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
            cf.completeExceptionally(new LearningSpaceException("Connection Failure",ErrorCode.CONNECTION_ERROR,HttpStatus.INTERNAL_SERVER_ERROR));
            }
        };
    }

    private static <T> void doErrorHandling(int statusCode,CompletableFuture<T>cf,String errorBody){

        if(statusCode>=502 && statusCode<=504){
            cf.completeExceptionally(new LearningSpaceException("ConnectionException", ErrorCode.CONNECTION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
        } else if (statusCode==401) {
            cf.completeExceptionally(new LearningSpaceException("UnAuthorized",ErrorCode.UNAUTHORIZED,HttpStatus.UNAUTHORIZED));
        }else {
            cf.completeExceptionally(new LearningSpaceException(errorBody,ErrorCode.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
