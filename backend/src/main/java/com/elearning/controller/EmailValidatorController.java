package com.elearning.controller;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.*;

@RestController
@RequestMapping("/api/validate-email")
public class EmailValidatorController {

    @Value("${easyemail.api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://easyemailapi.com/api/verify/";

    @GetMapping("/{email}")
    public ResponseEntity<Boolean> validateEmail(@PathVariable String email) {

        String urlString;
        try {
            urlString = BASE_URL + URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().build();
        }

        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(connection.getInputStream());
                boolean isValid = json.path("valid_mx").asBoolean(false) && !json.path("disposable").asBoolean(true);
                System.out.println("Email validation result for " + email + ": " + isValid);
                System.out.println("API response: " + json.toString());
                return ResponseEntity.ok(isValid);
            } else {
                System.out.println("Error: HTTP response code " + responseCode);
                return ResponseEntity.status(responseCode).build();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            int statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            return ResponseEntity.status(statusCode).build();
        }
    }

}
