package com.example.finquest.controller;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.bo.UpdateProfileRequest;
import com.example.finquest.bo.friendship.AddFriendRequest;
import com.example.finquest.bo.friendship.SendMessageRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.example.finquest.services.AchievementService;
import com.example.finquest.services.ChildUserService;
import com.example.finquest.services.FriendshipService;
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

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a child endpoint!";
    }

    @PostMapping("/stock-transaction")
    public ResponseEntity<Map<String, Object>> makeStockTransaction(@RequestHeader("Authorization") String token, @RequestBody StockTransactionRequest request) {
        return childUserService.makeStockTransaction(request, token);
    }

    @GetMapping("/get-stocks")
    public ResponseEntity<Map<String, Object>> getStocks(@RequestHeader("Authorization") String token) {
        return childUserService.getStocks(token);
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

    @GetMapping("/get-achievements")
    public ResponseEntity<Map<String, Object>> getAchievements(@RequestHeader("Authorization") String token) {
        return childUserService.getAchievements(token);
    }

    @GetMapping("/get-friends")
    public ResponseEntity<Map<String, Object>> getFriends(@RequestHeader("Authorization") String token) {
        return friendshipService.getFriends(token);
    }

    @PostMapping("/add-friend")
    public ResponseEntity<Map<String, Object>> addFriend(@RequestHeader("Authorization") String token, @RequestBody AddFriendRequest request) {
        return friendshipService.addFriend(token, request);
    }

    @PutMapping("/{childId}/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@PathVariable Long childId, @RequestParam Double amount) {
        return childUserService.deposit(childId, amount);
    }

    @PutMapping("/{childId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@PathVariable Long childId, @RequestParam Double amount) {
        return childUserService.withdraw(childId, amount);
    }

    @GetMapping("/get-chats")
    public ResponseEntity<Map<String, Object>> getChats(@RequestHeader("Authorization") String token) {
        return friendshipService.getChats(token);
    }

    @PostMapping("/send-message")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestHeader("Authorization") String token, @RequestBody SendMessageRequest request) {
        return friendshipService.sendMessage(token, request);
    }
}
