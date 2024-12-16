package com.example.finquest.controller;

import com.example.finquest.bo.ApprovalRequest;
import com.example.finquest.bo.ChildTransactionRequest;
import com.example.finquest.bo.ParentTransactionRequest;
import com.example.finquest.bo.ParentUserResponse;
import com.example.finquest.bo.RegisterChildUserRequest;
import com.example.finquest.services.AuthService;
import com.example.finquest.services.ParentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/parent")
public class ParentUserController {

    private final AuthService authService;
    private final ParentUserService parentUserService;

    @Autowired
    public ParentUserController(AuthService authService, ParentUserService parentUserService) {
        this.authService = authService;
        this.parentUserService = parentUserService;
    }

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a parent endpoint!";
    }

    @PostMapping("/register-child")
    public ResponseEntity<Map<String, String>> registerChild(@RequestBody RegisterChildUserRequest request, @RequestHeader("Authorization") String token) {
        return authService.registerChildUser(request, token);
    }

    @PostMapping("/make-transaction")
    public ResponseEntity<Map<String, String>> addBalanceToChild(@RequestBody ChildTransactionRequest request, @RequestHeader("Authorization") String token) {
        return parentUserService.makeTransactionForChild(request, token);
    }

    @PostMapping("/make-transaction-for-self")
    public ResponseEntity<Map<String, Object>> makeTransactionForParent(@RequestHeader("Authorization") String token, @RequestBody ParentTransactionRequest request) {
        return parentUserService.makeTransactionForParent(request, token);
    }

    @GetMapping("/get-parent")
    public ResponseEntity<ParentUserResponse> getParentUser(@RequestHeader("Authorization") String token) {
        ParentUserResponse response = parentUserService.getParentUser(token);
        return ResponseEntity.ok(response);  // Automatically serialized to JSON
    }

    @DeleteMapping("/remove-child/{childId}")
    public ResponseEntity<Map<String, String>> removeChild(@PathVariable Long childId, @RequestHeader("Authorization") String token) {
        return parentUserService.removeChildFromParent(childId, token);
    }

    @PutMapping("/update-transaction-permission/{childId}")
    public ResponseEntity<Map<String, String>> updateTransactionPermission(
            @PathVariable Long childId,
            @RequestBody ApprovalRequest approvalRequest,
            @RequestHeader("Authorization") String token) {
        return parentUserService.updateTransactionPermissionForChild(childId, approvalRequest, token);
    }




}