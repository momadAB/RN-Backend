package com.example.finquest.controller;

import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.services.AchievementService;
import com.example.finquest.services.ChildUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/child")
public class ChildUserController {

    @Autowired
    private ChildUserService childUserService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a child endpoint!";
    }

    @PostMapping("/stock-transaction")
    public ResponseEntity<Map<String, String>> makeStockTransaction(@RequestBody StockTransactionRequest request, @RequestHeader("Authorization") String token) {
        return childUserService.makeStockTransaction(request, token);
    }

    @PostMapping("/complete-achievement/{achievementId}")
    public ResponseEntity<Map<String, String>> completeAchievement(@PathVariable Long achievementId, @RequestHeader("Authorization") String token) {
        return achievementService.updateAchievementProgress(achievementId, token);
    }
}
