package com.example.finquest.services;

import com.example.finquest.bo.ChildTransactionRequest;
import com.example.finquest.bo.ParentUserResponse;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.ParentUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ParentUserService {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final JWTUtil jwtUtil;

    public ParentUserService (ParentUserRepository parentUserRepository, ChildUserRepository childUserRepository, JWTUtil jwtUtil) {
        this.parentUserRepository = parentUserRepository;
        this.childUserRepository = childUserRepository;
        this.jwtUtil = jwtUtil;
    }

    public ParentUserResponse getParentUserById(Long id) {
        ParentUserEntity parentUserEntity = parentUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent with ID " + id + " not found"));
        ParentUserResponse responseId = new ParentUserResponse(parentUserEntity);
        return responseId;
    }

    public ResponseEntity<Map<String, String>> makeTransactionForChild(ChildTransactionRequest request, String token) {
        try {
            String childName = request.getChildName();
            Double amount = request.getAmount();
            // Check that amount is not null
            if (amount == null || childName == null) {
                throw new IllegalArgumentException("Amount and child name cannot be null");
            }
            // Check token
            String username = jwtUtil.getUsernameFromToken(token);
            Optional<ParentUserEntity> parent = parentUserRepository.findByUsername(username);
            if (parent.isEmpty()) {
                throw new IllegalArgumentException("Parent user not found");
            }
            ChildUserEntity childUserEntity = childUserRepository.findByUsername(childName).orElseThrow(() -> new IllegalArgumentException("Child with name " + childName + " not found"));
            // Check if the child belongs to the parent
            if (!childUserEntity.getParentUser().getId().equals(parent.get().getId())) {
                throw new IllegalArgumentException("Child does not belong to the parent");
            }
            // Add balance
            childUserEntity.setBalance(childUserEntity.getBalance() + amount);
            childUserRepository.save(childUserEntity);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Balance added successfully");
            response.put("new balance", String.valueOf(childUserEntity.getBalance()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

}
