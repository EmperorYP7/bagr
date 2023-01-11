package com.example.bagr.controller;

import com.example.bagr.core.BagrException;
import com.example.bagr.helper.DashboardHelper;
import com.example.bagr.model.Executive;
import com.example.bagr.repository.ExecutiveRepo;
import com.example.bagr.view.ApiResponse;
import com.example.bagr.view.Status;
import com.example.bagr.view.dashboard.LoginResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class Dashboard {
    @Autowired
    private ExecutiveRepo executiveRepo;

    private final String AUTHORIZATION = "Authorization";

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestHeader(AUTHORIZATION) String authString) {
        ApiResponse.ApiResponseBuilder<LoginResponse> responseBuilder = ApiResponse.builder();
        try {
            // Find the user
            String[] decoded = DashboardHelper.decodeBasicAuth(authString);
            if(decoded == null) {
                throw new BagrException(400, "Invalid authorisation provided", BagrException.Reason.BAD_REQUEST);
            }
            String userName = decoded[0];
            Executive found = executiveRepo.findByUsername(userName);

            // Verify password
            String password = decoded[1];
            if(!DashboardHelper.isPasswordCorrect(password, found.getHashed_password())) {
                throw new BagrException(401, "Not authorised", BagrException.Reason.NOT_AUTHORISED);
            }

            // TODO - Generate key for this user
            String token = "magic key!";

            // Respond with key
            responseBuilder
                    .payload(LoginResponse.builder()
                                .token(token)
                            .build())
                    .status(new Status());
        } catch (BagrException e) {
            responseBuilder.status(e.toStatus());
        }

        return responseBuilder.build();
    }
}
