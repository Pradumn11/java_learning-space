package com.tech.learningspace.consumers.service;

import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.Utils.CompletableFutueUtils;
import com.tech.learningspace.consumers.Request.AddAuthProfileRequest;
import com.tech.learningspace.consumers.Response.AuthProfileResponse;
import com.tech.learningspace.consumers.dao.AuthProfileDao;
import com.tech.learningspace.consumers.dao.ConsumersDao;
import com.tech.learningspace.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletionStage;

@Service
public class AuthProfileService {

    private final AuthProfileDao authProfileDao;
    private final ConsumersDao consumersDao;


    @Autowired
    public AuthProfileService(AuthProfileDao authProfileDao, ConsumersDao consumersDao) {
        this.authProfileDao = authProfileDao;
        this.consumersDao = consumersDao;
    }


    public CompletionStage<AuthProfileResponse>addProfile(AddAuthProfileRequest authProfileRequest){
        return authProfileDao.getProfileByMobileOrEmail(authProfileRequest.getMobileNumber(),
                authProfileRequest.getEmail(),authProfileRequest.getTenantId())
                .thenCompose(authProfile->{
                   if(authProfile.isPresent()){
                       throw new LearningSpaceException("Mobile/Email Already Registered", ErrorCode.DUPLICATE_DATA,HttpStatus.BAD_REQUEST);
                    }
                   return authProfileDao.addAuthProfile(authProfileRequest);
                })
                .thenCompose(__->authProfileDao.getProfileByMobileOrEmail(authProfileRequest.getMobileNumber(),
                        authProfileRequest.getEmail(),authProfileRequest.getTenantId())
                                .thenApply(profile->profile.get()))
                .exceptionally(t->{
                    t= CompletableFutueUtils.unwrapCompletionStateException(t);
                    if (t instanceof LearningSpaceException){
                        throw new LearningSpaceException(t.getMessage(),((LearningSpaceException)t).getErrorCode(),((LearningSpaceException)t).getStatus());
                    }
                   throw  new LearningSpaceException(t.getMessage(),ErrorCode.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    public CompletionStage<AuthProfileResponse>getProfileByMobileOrEmail(String Mobile,String email,Long tenantId){
        return authProfileDao.getProfileByMobileOrEmail(Mobile,email,tenantId)
                .thenApply(
                        authProfile->{
                            if(!authProfile.isPresent()){
                                throw new LearningSpaceException("Mobile/Email not Registered",ErrorCode.NOT_EXISTS,HttpStatus.NOT_FOUND);
                            }
                            return authProfile.get();})
                .exceptionally(t->{
                    t= CompletableFutueUtils.unwrapCompletionStateException(t);
                    if (t instanceof LearningSpaceException){
                        throw new LearningSpaceException(t.getMessage(),((LearningSpaceException)t).getErrorCode(),((LearningSpaceException)t).getStatus());
                    }
                    throw  new LearningSpaceException(t.getMessage(),ErrorCode.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
                });

    }


}
