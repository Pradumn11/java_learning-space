package com.tech.learningspace.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class CompletableFutueUtils {
    public CompletableFutueUtils() {
    }

    public static <T>CompletionStage<T>unwrapCompletionStateException(CompletionStage<T>oldcompletionStage){
        CompletableFuture<T>newcompletableFuture=new CompletableFuture<>();
        oldcompletionStage.whenComplete((v,throwable)->{
            if(throwable==null){
                newcompletableFuture.complete(v);
            }else {
                newcompletableFuture.completeExceptionally(throwable);
            }
        });
        return newcompletableFuture;
    }
    public static Throwable unwrapCompletionStateException(Throwable throwable){
        int maxDepth=30;
        if (maxDepth<=0){
            maxDepth=2;
        }

        while (isCompletionException(throwable) && maxDepth-- >0){
            throwable=throwable.getCause();
        }
        return throwable;
    }
    public static boolean isCompletionException(Throwable ex){
        return null!=ex && CompletionException.class.isAssignableFrom(ex.getClass());
    }
}
