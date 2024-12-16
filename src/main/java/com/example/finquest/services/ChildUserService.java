package com.example.finquest.services;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.LoginRequest;
import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.bo.UpdateProfileRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.*;
import com.example.finquest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChildUserService {

    private final ChildUserRepository childUserRepository;
    private final ParentUserRepository parentUserRepository;
    private final OwnedStockRepository ownedStockRepository;
    private final AchievementProgressRepository achievementProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;;
    private final RoadmapIslandRepository roadmapIslandRepository;
    private final RoadmapLessonRepository roadmapLessonRepository;
    private final StockRepository stockRepository;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Autowired
    public ChildUserService(ChildUserRepository childUserRepository,
                            ParentUserRepository parentUserRepository,
                            OwnedStockRepository ownedStockRepository,
                            AchievementProgressRepository achievementProgressRepository,
                            LessonProgressRepository lessonProgressRepository,
                            RoadmapIslandRepository roadmapIslandRepository,
                            RoadmapLessonRepository roadmapLessonRepository,
                            StockRepository stockRepository,
                            AuthService authService,
                            BCryptPasswordEncoder passwordEncoder,
                            JWTUtil jwtUtil) {
        this.childUserRepository = childUserRepository;
        this.parentUserRepository = parentUserRepository;
        this.ownedStockRepository = ownedStockRepository;
        this.achievementProgressRepository = achievementProgressRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.roadmapIslandRepository = roadmapIslandRepository;
        this.roadmapLessonRepository = roadmapLessonRepository;
        this.stockRepository = stockRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ChildUserResponse getChildUserById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Child with ID " + id + " not found"));

        return new ChildUserResponse(childUserEntity);
    }

    public void deleteChildById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Child with ID " + id + " not found"));

        childUserRepository.delete(childUserEntity);
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
            }).collect(Collectors.toList());

            response.put("achievements", achievements);

            // Get roadmap islands
            List<RoadmapIslandEntity> islands = roadmapIslandRepository.findAll();
            List<Map<String, Object>> islandData = islands.stream().map(island -> {
                Map<String, Object> islandMap = new HashMap<>();
                islandMap.put("id", island.getId());
                islandMap.put("title", island.getTitle());
                islandMap.put("logoUrl", island.getLogoUrl());

                // Total lessons in the island
                int totalLessons = island.getLessons().size();

                // Calculate completed lessons for this island
                long completedLessonsCount = island.getLessons().stream().filter(l ->
                        lessonProgressRepository.findByChildUserAndRoadmapLesson(childUserEntity, l)
                                .map(LessonProgressEntity::getCompleted)
                                .orElse(false)
                ).count();

                double completionPercentage = totalLessons > 0 ? ((double) completedLessonsCount / totalLessons) * 100 : 0;

                // Store total lessons, completed lessons, and completion percentage in island map
                islandMap.put("totalLessons", totalLessons);
                islandMap.put("completedLessons", completedLessonsCount);
                islandMap.put("completionPercentage", completionPercentage);

                // Get lessons for this island
                List<Map<String, Object>> lessons = island.getLessons().stream().map(lesson -> {
                    Map<String, Object> lessonMap = new HashMap<>();
                    lessonMap.put("id", lesson.getId());
                    lessonMap.put("title", lesson.getTitle());
                    lessonMap.put("description", lesson.getDescription());
                    return lessonMap;
                }).collect(Collectors.toList());

                islandMap.put("lessons", lessons);
                return islandMap;
            }).collect(Collectors.toList());

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

    public ResponseEntity<Map<String, Object>> completeLesson(String token, Long lessonId) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            ChildUserEntity childUserEntity = childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get lesson entity
            RoadmapLessonEntity lessonEntity = roadmapLessonRepository.findById(lessonId)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

            // Get or create lesson progress entity
            LessonProgressEntity lessonProgressEntity = lessonProgressRepository.findByChildUserAndRoadmapLesson(childUserEntity, lessonEntity)
                    .orElseGet(() -> {
                        LessonProgressEntity newProgress = new LessonProgressEntity(childUserEntity, lessonEntity);
                        lessonProgressRepository.save(newProgress); // Save the new progress entity
                        return newProgress;
                    });

            // Set completed to true and save the updated progress
            lessonProgressEntity.setCompleted(true);
            lessonProgressRepository.save(lessonProgressEntity);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lesson completed successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> updateProfile(String token, UpdateProfileRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Check that request username is not already taken
        if (childUserRepository.findByUsername(request.getUsername()).isPresent() || parentUserRepository.findByUsername(request.getUsername()).isPresent()) {
            response.put("error", "Username already taken");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check if the token is missing
        if (token == null || token.trim().isEmpty()) {
            response.put("error", "Missing or invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Check if the request is null, empty, or invalid
        if (request == null || request.getAvatarId() == null || request.getAvatarId() <= 0 ||
                request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            response.put("error", "Request must have a valid avatarId, username, and password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Fetch child user entity
        String username = jwtUtil.getUsernameFromToken(token);
        ChildUserEntity childUserEntity = childUserRepository.findByUsername(username).orElse(null);

        if (childUserEntity == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Set up new child entity
        try {
            childUserEntity.setAvatarId(request.getAvatarId());
            childUserEntity.setUsername(request.getUsername());
            childUserEntity.setPassword(passwordEncoder.encode(request.getPassword()));

            // Save child entity
            childUserRepository.save(childUserEntity);
        } catch (Exception e) {
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Profile updated successfully");

        // Get new token
        Map<String, String> newToken = authService.loginUser(new LoginRequest(request.getUsername(), request.getPassword())).getBody();
        response.put("response", newToken);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
