package com.example.finquest.controller;

import com.example.finquest.bo.RegisterChildUserRequest;
import com.example.finquest.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parent")
public class ParentUserController {

    private final AuthService authService;

    @Autowired
    public ParentUserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a parent endpoint!";
    }

    @PostMapping("/register-child")
    public ResponseEntity<String> registerChild(@RequestBody RegisterChildUserRequest request, @RequestHeader("Authorization") String token) {
        try {
            String message = authService.registerChild(request, token);
            return ResponseEntity.ok().body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}