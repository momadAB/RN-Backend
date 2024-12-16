package com.example.finquest.services;

import com.example.finquest.bo.ApprovalRequest;
import com.example.finquest.bo.ChildTransactionRequest;
import com.example.finquest.bo.ParentTransactionRequest;
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

    public ParentUserResponse getParentUser(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        System.out.println("Extracted Username: " + username);

        ParentUserEntity parentUserEntity = parentUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Parent with name " + username + " not found"));
        System.out.println("Children: " + parentUserEntity.getChildren());

        return new ParentUserResponse(parentUserEntity);
    }

    public ResponseEntity<Map<String, Object>> makeTransactionForParent(ParentTransactionRequest request, String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            Optional<ParentUserEntity> parent = parentUserRepository.findByUsername(username);
            if (parent.isEmpty()) {
                throw new IllegalArgumentException("Parent user not found");
            }
            if (request.getAmount() == null || request.getAmount() == 0.0) {
                throw new IllegalArgumentException("Amount cannot be 0 or null");
            }

            parent.get().setBalance(parent.get().getBalance() + request.getAmount());
            parentUserRepository.save(parent.get());

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Balance changed successfully");
            response.put("newBalance", parent.get().getBalance());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
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

            // Check if the parent has enough balance
            if (parent.get().getBalance() < amount) {
                throw new IllegalArgumentException("Parent does not have enough balance");
            }

            // Reduce amount from parents balance
            parent.get().setBalance(parent.get().getBalance() - amount);
            parentUserRepository.save(parent.get());

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

    public ResponseEntity<Map<String, String>> removeChildFromParent(Long childId, String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            ParentUserEntity parentUser = parentUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

            ChildUserEntity childUser = childUserRepository.findById(childId)
                    .orElseThrow(() -> new IllegalArgumentException("Child not found"));

            if (!childUser.getParentUser().getId().equals(parentUser.getId())) {
                throw new IllegalArgumentException("Child does not belong to the parent");
            }

            parentUser.getChildren().remove(childUser);
            childUserRepository.delete(childUser);

            parentUserRepository.save(parentUser);

            Map<String, String> response = Map.of("message", "Child removed successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred"));
        }
    }


    public ResponseEntity<Map<String, String>> updateTransactionPermissionForChild(Long childId, ApprovalRequest approvalRequest, String token) {
        try {
            // Get parent username from token
            String username = jwtUtil.getUsernameFromToken(token);
            Optional<ParentUserEntity> parent = parentUserRepository.findByUsername(username);
            if (parent.isEmpty()) {
                throw new IllegalArgumentException("Parent user not found");
            }

            // Find the child by ID
            ChildUserEntity childUserEntity = childUserRepository.findById(childId)
                    .orElseThrow(() -> new IllegalArgumentException("Child not found"));

            // Ensure the child belongs to the parent
            if (!childUserEntity.getParentUser().getId().equals(parent.get().getId())) {
                throw new IllegalArgumentException("Child does not belong to the parent");
            }

            // Update the permission status using the request body
            childUserEntity.setAllowedToMakeTransactionsWithNoPermission(approvalRequest.isAllowedToMakeTransactionsWithNoPermission());
            childUserRepository.save(childUserEntity); // Save the updated child entity

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Permission updated successfully");
            response.put("new permission status", String.valueOf(childUserEntity.isAllowedToMakeTransactionsWithNoPermission()));
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred"));
        }
    }




}
