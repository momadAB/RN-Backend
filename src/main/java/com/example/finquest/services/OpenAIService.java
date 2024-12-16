package com.example.finquest.services;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {

    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final String sampleResponse = "";

    @Value("${key:default}")
    private String API_KEY;
    private String instructions = "";

    public Map<String, String> getChatGPTResponse(String prompt) {

        System.out.println("API KEY : " + API_KEY);

        RestTemplate restTemplate = new RestTemplate();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        Object messageBody = new Object[]{
                Map.of("role", "system", "content", instructions),
                Map.of("role", "user", "content", prompt)
        };
        requestBody.put("messages", messageBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Make API call
            ResponseEntity<String> response =
                    restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // Parse response body
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());
            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

            // Prepare JSON response
            Map<String, String> jsonResponse = new HashMap<>();
            jsonResponse.put("response", content);

            return jsonResponse;
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("response", "Error: " + e.getMessage());
            return errorResponse;
        }
    }

}
