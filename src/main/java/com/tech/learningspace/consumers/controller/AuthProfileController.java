package com.tech.learningspace.consumers.controller;

import com.tech.learningspace.consumers.Request.AddAuthProfileRequest;
import com.tech.learningspace.consumers.Response.AuthProfileResponse;
import com.tech.learningspace.consumers.service.AuthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/learning-space")
public class AuthProfileController {

    public final AuthProfileService authProfileService;

    @Autowired
    public AuthProfileController(AuthProfileService authProfileService) {
        this.authProfileService = authProfileService;
    }

    @PostMapping("/addProfile")
    public CompletionStage<AuthProfileResponse>addProfile(@RequestBody AddAuthProfileRequest authProfileRequest){
        return authProfileService.addProfile(authProfileRequest);
    }

    @GetMapping("/getProfile")
    public CompletionStage<AuthProfileResponse>getProfile(@RequestParam("mobileNumber")String MobileNumber,
                                                                  @RequestParam("email")String Email,
                                                                  @RequestParam("tenant")Long Tenant){
        return authProfileService.getProfileByMobileOrEmail(MobileNumber,Email,Tenant);
    }

}
