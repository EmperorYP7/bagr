package com.example.bagr.helper;

import com.example.bagr.core.EnvManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DashboardHelper {

    public static String[] decodeBasicAuth(String authorization) {
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            return credentials.split(":", 2);
        }
        else {
            return null;
        }
    }

    public static String getSHA256Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, digest);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 64)
            {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isPasswordCorrect(String password, String hash) {
        return DashboardHelper.getSHA256Hash(password).equals(hash);
    }

    public static String generateToken(String subject, Map<String, String> claims, String secret) {

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        String token = Jwts.builder()
                    .setSubject(subject)
                    .setId(UUID.randomUUID().toString())
                    .setClaims(claims)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plus(5L, ChronoUnit.HOURS)))
                    .signWith(hmacKey)
                .compact();

        return token;
    }

    private static String getJWTString(String bearerToken) {
        if(bearerToken != null && bearerToken.toLowerCase().startsWith("bearer")) {
            String trimmedToken = bearerToken.substring("Bearer ".length()).trim();
            return trimmedToken;
        } else {
            return "";
        }
    }

    public static Claims getClaims(String bearerToken, String secret) {

        String jwtString = getJWTString(bearerToken);

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt.getBody();
    }
}
