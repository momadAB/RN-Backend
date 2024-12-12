package com.example.finquest.controller;

import com.example.finquest.bo.RegisterParentUserRequest;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class PublicController {

    private final AuthService authService;

    @Autowired
    public PublicController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String publicHello() {
        return "Hello, this is a public endpoint!";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterParentUserRequest request) {
        return authService.registerParentUser(request);
    }

    @PostMapping("/login-child")
    public ResponseEntity<Map<String, String>> loginChild(@RequestBody ChildUserEntity childUser) {
        return authService.loginChildUser(childUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody ParentUserEntity parentUser) {
        return authService.loginParentUser(parentUser);
    }
}
