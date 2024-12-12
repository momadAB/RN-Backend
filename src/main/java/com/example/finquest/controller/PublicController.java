package com.example.finquest.controller;

import com.example.finquest.bo.RegisterParentUserRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ParentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class PublicController {

    private final ParentUserRepository parentUserRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PublicController(
            ParentUserRepository parentUserRepository,
            JWTUtil jwtUtil,
            AuthenticationManager authenticationManager,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.parentUserRepository = parentUserRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/hello")
    public String publicHello() {
        return "Hello, this is a public endpoint!";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterParentUserRequest request) {
        try {
            ParentUserEntity parent = new ParentUserEntity();
            parent.setUsername(request.getUsername());
            parent.setPassword(passwordEncoder.encode(request.getPassword()));
            parent.setRoles(request.getRoles());
            parentUserRepository.save(parent);
            return ResponseEntity.ok().body("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody ParentUserEntity parentUser) {
        try {
            ParentUserEntity existingUser = parentUserRepository.findByUsername(parentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if password matches
            if (!passwordEncoder.matches(parentUser.getPassword(), existingUser.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", existingUser.getRoles());

            String token = jwtUtil.generateToken(existingUser.getUsername(), existingUser.getRoles(), claims);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
