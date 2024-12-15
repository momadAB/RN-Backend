package com.example.finquest.services;

import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.AchievementProgressEntity;
import com.example.finquest.repository.AchievementProgressRepository;
import com.example.finquest.repository.AchievementRepository;
import com.example.finquest.repository.ChildUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AchievementService {

    private final AchievementProgressRepository achievementProgressRepository;
    private final AchievementRepository achievementRepository;
    private final ChildUserRepository childUserRepository;
    private final JWTUtil jwtUtil;

    public AchievementService(AchievementProgressRepository achievementProgressRepository,
                              AchievementRepository achievementRepository,
                              ChildUserRepository childUserRepository,
                              JWTUtil jwtUtil) {
        this.achievementProgressRepository = achievementProgressRepository;
        this.achievementRepository = achievementRepository;
        this.childUserRepository = childUserRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<Map<String, String>> updateAchievementProgress(Long achievementId, String token) {
        // Assume checking achievement conditions is done elsewhere
        try {
        // Extract child username from token and get childUserEntity
        String username = jwtUtil.getUsernameFromToken(token);
        Long childUserId = childUserRepository.findByUsername(username).get().getId();
        // Make achievement isCompleted true and save
        AchievementProgressEntity achievementProgress = new AchievementProgressEntity();
        achievementProgress.setChildUser(childUserRepository.findById(childUserId).get());
        achievementProgress.setAchievement(achievementRepository.findById(achievementId).get());
        achievementProgress.setIsCompleted(true);
        achievementProgressRepository.save(achievementProgress);

        return ResponseEntity.ok(Map.of("message", "Achievement completed successfully!"));
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
