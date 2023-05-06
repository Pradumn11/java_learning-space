package com.tech.learningspace.Utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LearningSpaceThreadFactory {
    public LearningSpaceThreadFactory() {
    }
    private static final Integer KEEP_ALIVE_TIME=3;

    public static ThreadPoolExecutor createThreadPoolExecutor(String instanceOfClass, Integer threadCount){
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(threadCount,threadCount,KEEP_ALIVE_TIME, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat(instanceOfClass).build());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
