package com.example.finquest.services;

import com.example.finquest.bo.LoginRequest;
import com.example.finquest.bo.RegisterChildUserRequest;
import com.example.finquest.bo.RegisterParentUserRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.ParentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            ParentUserRepository parentUserRepository,
            ChildUserRepository childUserRepository,
            JWTUtil jwtUtil,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.parentUserRepository = parentUserRepository;
        this.childUserRepository = childUserRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<Map<String, String>> registerParentUser(RegisterParentUserRequest request) {
        try {
            validateParentInput(request);
            ensureUsernameIsUnique(request.getUsername());
            ParentUserEntity parent = new ParentUserEntity();
            parent.setUsername(request.getUsername());
            parent.setPassword(passwordEncoder.encode(request.getPassword()));
            parent.setRoles(request.getRoles());
            parentUserRepository.save(parent);
            return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, String>> registerChildUser(RegisterChildUserRequest request, String token) {
        try {
            validateChildInput(request);
            String username = extractUsernameFromToken(token);
            ParentUserEntity parent = getParentByUsername(username);
            ensureUsernameIsUnique(request.getUsername());
            ChildUserEntity child = createChildUser(request, parent);
            childUserRepository.save(child);
            parent.getChildren().add(child);
            parentUserRepository.save(parent);
            return ResponseEntity.ok(Map.of("message", "Child registered successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, String>> loginUser(LoginRequest loginRequest) {
        try {
            // Step 1: Validate input
            validateLoginInput(loginRequest.getUsername(), loginRequest.getPassword());

            // Step 2: Try to find the user in the parent repository first
            ParentUserEntity parentUser = parentUserRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            ChildUserEntity childUser = null;

            if (parentUser == null) {
                // If not found in parent, try to find in the child repository
                childUser = childUserRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            }

            // Step 3: Determine which user was found and verify password
            if (parentUser != null) {
                if (!passwordEncoder.matches(loginRequest.getPassword(), parentUser.getPassword())) {
                    throw new RuntimeException("Invalid password");
                }
                // Step 4: Generate JWT token for parent user
                Map<String, Object> claims = new HashMap<>();
                claims.put("roles", parentUser.getRoles());
                String token = jwtUtil.generateToken(parentUser.getUsername(), parentUser.getRoles(), claims);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("userType", "parent");
                // Step 5: Return success response
                return ResponseEntity.ok(response);
            } else {
                if (!passwordEncoder.matches(loginRequest.getPassword(), childUser.getPassword())) {
                    throw new RuntimeException("Invalid password");
                }
                // Step 4: Generate JWT token for child user
                Map<String, Object> claims = new HashMap<>();
                claims.put("roles", childUser.getRoles());
                String token = jwtUtil.generateToken(childUser.getUsername(), childUser.getRoles(), claims);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("userType", "child");
                // Step 5: Return success response
                return ResponseEntity.ok(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }


    protected void validateChildInput(RegisterChildUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (request.getAvatarId() == null) {
            throw new IllegalArgumentException("Avatar ID cannot be null");
        }
    }

    protected void validateParentInput(RegisterParentUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    protected void validateLoginInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    protected String extractUsernameFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUsernameFromToken(token);
    }

    private ParentUserEntity getParentByUsername(String username) {
        return parentUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent user not found"));
    }

    private void ensureUsernameIsUnique(String username) {
        if (childUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (parentUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private ChildUserEntity createChildUser(RegisterChildUserRequest request, ParentUserEntity parent) {
        ChildUserEntity child = new ChildUserEntity();
        child.setUsername(request.getUsername());
        child.setPassword(passwordEncoder.encode(request.getPassword()));
        child.setRoles(request.getRoles());
        child.setAvatarId(request.getAvatarId());
        child.setParentUser(parent);
        return child;
    }
}
