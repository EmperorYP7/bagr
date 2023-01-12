package com.example.bagr.controller;

import com.example.bagr.core.BagrException;
import com.example.bagr.helper.DashboardHelper;
import com.example.bagr.model.Executive;
import com.example.bagr.repository.ExecutiveRepo;
import com.example.bagr.view.ApiResponse;
import com.example.bagr.view.Status;
import com.example.bagr.view.dashboard.ItineraryResponse;
import com.example.bagr.view.dashboard.LoginResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/dashboard")
public class Dashboard {
    @Autowired
    private ExecutiveRepo executiveRepo;

    private final String AUTHORIZATION = "Authorization";

    @Value("${server.auth.secret}")
    private String API_KEY;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestHeader(AUTHORIZATION) String authString) {
        ApiResponse.ApiResponseBuilder<LoginResponse> responseBuilder = ApiResponse.builder();
        try {
            String[] decoded = DashboardHelper.decodeBasicAuth(authString);
            if(decoded == null) {
                throw new BagrException(400, "Invalid authorisation provided", BagrException.Reason.BAD_REQUEST);
            }

            // Find the user
            String userName = decoded[0];
            Executive found = executiveRepo.findByUsername(userName);
            if(found == null) {
                throw new BagrException(404, "User not found", BagrException.Reason.NOT_FOUND);
            }

            // Verify password
            String password = decoded[1];
            if(!DashboardHelper.isPasswordCorrect(password, found.getHashed_password())) {
                throw new BagrException(401, "Not authorised", BagrException.Reason.NOT_AUTHORISED);
            }

            HashMap<String, String> claimMap = new HashMap<>();

            claimMap.put("username", userName);
            claimMap.put("exec_id", String.valueOf(found.getId()));
            claimMap.put("name", found.getName());

            // Generate key for this user
            String token = DashboardHelper.generateToken(userName, claimMap, API_KEY);

            // Respond with key
            responseBuilder
                    .payload(LoginResponse.builder()
                                .token(token)
                            .build())
                    .status(new Status());
        } catch (BagrException e) {
            responseBuilder.status(e.toStatus());
        } catch (Exception e) {
            responseBuilder.status(Status.builder()
                            .code(400)
                            .message(e.getMessage())
                            .reason(BagrException.Reason.INTERNAL_SERVER_ERROR.toString())
                    .build());
        }

        return responseBuilder.build();
    }
}
