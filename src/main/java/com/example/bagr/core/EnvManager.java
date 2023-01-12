package com.example.bagr.core;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class EnvManager {

    @Value("${server.auth.secret}")
    private String API_SECRET_KEY;

    private static EnvManager instance = new EnvManager();

    @Autowired
    public EnvManager() {}

    public static EnvManager getInstance() {
        return instance;
    }
}
