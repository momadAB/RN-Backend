package com.example.finquest.services;

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

    public ResponseEntity<String> registerParentUser(RegisterParentUserRequest request) {
        try {
            validateParentInput(request);
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

    public String registerChild(RegisterChildUserRequest request, String token) {
        validateChildInput(request);

        String username = extractUsernameFromToken(token);
        ParentUserEntity parent = getParentByUsername(username);

        ensureChildNameIsUnique(parent, request.getName());

        ChildUserEntity child = createChildUser(request, parent);
        childUserRepository.save(child);

        parent.getChildren().add(child);
        parentUserRepository.save(parent);

        return "Child registered successfully!";
    }

    public ResponseEntity<Map<String, String>> loginChildUser(ChildUserEntity childUser) {
        try {
            ChildUserEntity existingChildUser = childUserRepository.findByName(childUser.getName())
                    .orElseThrow(() -> new RuntimeException("Child user not found"));

            if (!passwordEncoder.matches(childUser.getPassword(), existingChildUser.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", existingChildUser.getRoles());

            String token = jwtUtil.generateToken(existingChildUser.getName(), existingChildUser.getRoles(), claims);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, String>> loginParentUser(ParentUserEntity parentUser) {
        try {
            ParentUserEntity existingUser = parentUserRepository.findByUsername(parentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

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

    private void validateChildInput(RegisterChildUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (request.getAvatarId() == null) {
            throw new IllegalArgumentException("Avatar ID cannot be null");
        }
    }

    private void validateParentInput(RegisterParentUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    private String extractUsernameFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUsernameFromToken(token);
    }

    private ParentUserEntity getParentByUsername(String username) {
        return parentUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent user not found"));
    }

    private void ensureChildNameIsUnique(ParentUserEntity parent, String childName) {
        boolean childExists = parent.getChildren().stream()
                .anyMatch(child -> child.getName().equalsIgnoreCase(childName));
        if (childExists) {
            throw new IllegalArgumentException("A child with the same name already exists under this parent");
        }
    }

    private ChildUserEntity createChildUser(RegisterChildUserRequest request, ParentUserEntity parent) {
        ChildUserEntity child = new ChildUserEntity();
        child.setName(request.getName());
        child.setPassword(passwordEncoder.encode(request.getPassword()));
        child.setRoles(request.getRoles());
        child.setAvatarId(request.getAvatarId());
        child.setParentUser(parent);
        return child;
    }
}
