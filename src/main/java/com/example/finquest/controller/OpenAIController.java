package com.example.finquest.controller;


import com.example.finquest.bo.ChatRequest;
import com.example.finquest.services.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/api/v1/user/chat")
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        return openAIService.getChatGPTResponse(request.getPrompt());
    }
}
