package com.tech.learningspace.consumers.service;

import com.tech.learningspace.Exception.AuthProfileException;
import com.tech.learningspace.Utils.CompletableFutueUtils;
import com.tech.learningspace.consumers.Request.AddAuthProfileRequest;
import com.tech.learningspace.consumers.Response.AuthProfileResponse;
import com.tech.learningspace.consumers.dao.AuthProfileDao;
import com.tech.learningspace.consumers.dao.ConsumersDao;
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
                       throw new AuthProfileException("Error_01","Mobile/Email Already Registered",HttpStatus.BAD_REQUEST);
                    }
                   return authProfileDao.addAuthProfile(authProfileRequest);})
                .thenCompose(__->authProfileDao.getProfileByMobileOrEmail(authProfileRequest.getMobileNumber(),
                        authProfileRequest.getEmail(),authProfileRequest.getTenantId())
                                .thenApply(profile->profile.get()))
                .exceptionally(t->{
                    t= CompletableFutueUtils.unwrapCompletionStateException(t);
                   throw  new AuthProfileException("Error_01",t.getMessage(),HttpStatus.BAD_REQUEST);
                });
    }

    public CompletionStage<AuthProfileResponse>getProfileByMobileOrEmail(String Mobile,String email,Long tenantId){
        return authProfileDao.getProfileByMobileOrEmail(Mobile,email,tenantId)
                .thenApply(
                        authProfile->{
                            if(!authProfile.isPresent()){
                                throw new AuthProfileException("Error_01","Mobile/Email not Registered",HttpStatus.BAD_REQUEST);
                            }
                            return authProfile.get();})
                .exceptionally(t->{
                    t= CompletableFutueUtils.unwrapCompletionStateException(t);
                    throw  new AuthProfileException("Error_02",t.getMessage(),HttpStatus.BAD_REQUEST);
                });

    }


}
