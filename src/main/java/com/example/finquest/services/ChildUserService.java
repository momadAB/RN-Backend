package com.example.finquest.services;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.*;
import com.example.finquest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChildUserService {

    private final ChildUserRepository childUserRepository;
    private final OwnedStockRepository ownedStockRepository;
    private final AchievementProgressRepository achievementProgressRepository;
    private final RoadmapIslandRepository roadmapIslandRepository;
    private final RoadmapLessonRepository roadmapLessonRepository;
    private final StockRepository stockRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public ChildUserService(ChildUserRepository childUserRepository,
                            OwnedStockRepository ownedStockRepository,
                            AchievementProgressRepository achievementProgressRepository,
                            RoadmapIslandRepository roadmapIslandRepository,
                            RoadmapLessonRepository roadmapLessonRepository,
                            StockRepository stockRepository,
                            JWTUtil jwtUtil) {
        this.childUserRepository = childUserRepository;
        this.ownedStockRepository = ownedStockRepository;
        this.achievementProgressRepository = achievementProgressRepository;
        this.roadmapIslandRepository = roadmapIslandRepository;
        this.roadmapLessonRepository = roadmapLessonRepository;
        this.stockRepository = stockRepository;
        this.jwtUtil = jwtUtil;
    }

    public ChildUserResponse getChildUserById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Child with ID " + id + " not found"));
        ChildUserResponse childUserResponse = new ChildUserResponse(childUserEntity);
        return childUserResponse;
    }

    public ResponseEntity<Map<String, Object>> makeStockTransaction(StockTransactionRequest request, String token) {
        try {
            // Extract child username from token
            String username = jwtUtil.getUsernameFromToken(token);

            // Check if child user exists
            ChildUserEntity childUserEntity = childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Find the stock
            StockEntity stockEntity = stockRepository.findByCompanyName(request.getCompanyName())
                    .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

            // Check if child owns the stock
            Optional<OwnedStockEntity> ownedStock = ownedStockRepository.findByChildUserAndStock(childUserEntity, stockEntity);

            // Create response
            Map<String, Object> response = new HashMap<>();

            if (ownedStock.isPresent()) {
                OwnedStockEntity ownedStockEntity = ownedStock.get();
                Double newAmount = ownedStockEntity.getAmount() + request.getAmountChange();

                if (newAmount < 0) {
                    throw new IllegalArgumentException("Stock amount cannot be negative");
                }

                if (newAmount == 0) {
                    ownedStockRepository.delete(ownedStockEntity);
                    response.put("message", "Stock deleted successfully");
                } else {
                    Double balance = childUserEntity.getBalance() != null ? childUserEntity.getBalance() : 0.0;
                    if (balance < request.getAmountChange() * stockEntity.getStockPrice()) {
                        throw new IllegalArgumentException("Insufficient balance for this transaction");
                    }

                    childUserEntity.setBalance(balance - (request.getAmountChange() * stockEntity.getStockPrice()));
                    childUserRepository.save(childUserEntity);

                    ownedStockEntity.setAmount(newAmount);
                    ownedStockRepository.save(ownedStockEntity);

                    response.put("message", "Stock updated successfully");
                    response.put("stockAmount", ownedStockEntity.getAmount());
                }
            } else {
                OwnedStockEntity ownedStockEntity = new OwnedStockEntity(childUserEntity, stockEntity, request.getAmountChange());

                if (ownedStockEntity.getAmount() < 0) {
                    throw new IllegalArgumentException("Stock amount cannot be negative");
                }

                Double balance = childUserEntity.getBalance() != null ? childUserEntity.getBalance() : 0.0;
                if (balance < request.getAmountChange() * stockEntity.getStockPrice()) {
                    throw new IllegalArgumentException("Insufficient balance for this transaction");
                }

                childUserEntity.setBalance(balance - request.getAmountChange() * stockEntity.getStockPrice());
                childUserRepository.save(childUserEntity);

                ownedStockRepository.save(ownedStockEntity);

                response.put("message", "Stock added successfully");
                response.put("stockAmount", ownedStockEntity.getAmount());
            }

            response.put("childUsername", childUserEntity.getUsername());
            response.put("newBalance", childUserEntity.getBalance());
            response.put("companyName", stockEntity.getCompanyName());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> getProgress(String token) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            ChildUserEntity childUserEntity = childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get child's progressEntity
            List<AchievementProgressEntity> progressEntities = achievementProgressRepository.findByChildUser(childUserEntity);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("childUsername", childUserEntity.getUsername());
            response.put("balance", childUserEntity.getBalance());

            List<Map<String, Object>> achievements = progressEntities.stream().map(progressEntity -> {
                AchievementEntity achievementEntity = progressEntity.getAchievement();
                Map<String, Object> achievementData = new HashMap<>();
                achievementData.put("title", achievementEntity.getTitle());
                achievementData.put("description", achievementEntity.getDescription());
                achievementData.put("logoUrl", achievementEntity.getLogoUrl());
                achievementData.put("id", achievementEntity.getId());
                achievementData.put("isCompleted", progressEntity.getIsCompleted());
                return achievementData;
            }).toList();

            response.put("achievements", achievements);

            // Get roadmap islands
            List<RoadmapIslandEntity> islands = roadmapIslandRepository.findAll();
            List<Map<String, Object>> islandData = islands.stream().map(island -> {
                Map<String, Object> islandMap = new HashMap<>();
                islandMap.put("id", island.getId());
                islandMap.put("title", island.getTitle());
                islandMap.put("logoUrl", island.getLogoUrl());
                return islandMap;
            }).toList();

            // TODO: Get progress for each island

            response.put("islands", islandData);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getLessons(String token, Long islandId) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get Island Entity
            RoadmapIslandEntity islandEntity = roadmapIslandRepository.findById(islandId)
                    .orElseThrow(() -> new IllegalArgumentException("Island not found"));

            // Get Lessons that are under the islandId
            List<RoadmapLessonEntity> lessons = islandEntity.getLessons();

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("lessons", lessons);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPages(String token, Long lessonId) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get lesson entity
            RoadmapLessonEntity lessonEntity = roadmapLessonRepository.findById(lessonId)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

            // Get pages that are under the lessonId
            List<RoadmapPageEntity> pages = lessonEntity.getPages();

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("pages", pages);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
