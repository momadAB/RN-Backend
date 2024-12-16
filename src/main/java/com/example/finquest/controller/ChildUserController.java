package com.example.finquest.controller;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.bo.UpdateProfileRequest;
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
    public ResponseEntity<Map<String, Object>> makeStockTransaction(@RequestHeader("Authorization") String token, @RequestBody StockTransactionRequest request) {
        return childUserService.makeStockTransaction(request, token);
    }

    @PostMapping("/complete-achievement/{achievementId}")
    public ResponseEntity<Map<String, String>> completeAchievement(@RequestHeader("Authorization") String token, @PathVariable Long achievementId) {
        return achievementService.updateAchievementProgress(achievementId, token);
    }

    @GetMapping("/get-progress")
    public ResponseEntity<Map<String, Object>> getProgress(@RequestHeader("Authorization") String token) {
        return childUserService.getProgress(token);
    }

    @GetMapping("/get-lessons/{islandId}")
    public ResponseEntity<Map<String, Object>> getLessons(@RequestHeader("Authorization") String token, @PathVariable Long islandId) {
        return childUserService.getLessons(token, islandId);
    }

    @GetMapping("/get-pages/{lessonId}")
    public ResponseEntity<Map<String, Object>> getPages(@RequestHeader("Authorization") String token, @PathVariable Long lessonId) {
        return childUserService.getPages(token, lessonId);
    }

    @PostMapping("/complete-lesson/{lessonId}")
    public ResponseEntity<Map<String, Object>> completeLesson(@RequestHeader("Authorization") String token, @PathVariable Long lessonId) {
        return childUserService.completeLesson(token, lessonId);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestHeader("Authorization") String token, @RequestBody UpdateProfileRequest request) {
        return childUserService.updateProfile(token, request);
    }

    @GetMapping("/get-child/{id}")
    public ResponseEntity<ChildUserResponse> getChildUserById(@PathVariable Long id) {
        ChildUserResponse response = childUserService.getChildUserById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-child/{id}")
    public ResponseEntity<String> deleteChildById(@PathVariable Long id) {
        childUserService.deleteChildById(id);
        return ResponseEntity.ok("Child with ID " + id + " has been deleted successfully.");
    }
}
