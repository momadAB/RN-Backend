package com.example.finquest.services;

import com.example.finquest.bo.*;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.*;
import com.example.finquest.repository.*;
import com.example.finquest.view.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
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
    private final AchievementRepository achievementRepository;
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
                            AchievementRepository achievementRepository,
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
        this.achievementRepository = achievementRepository;
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
            // Validate input
            validateStockTransactionRequest(request);

            // Get child and stock
            ChildUserEntity childUserEntity = getChildUserByUsername(jwtUtil.getUsernameFromToken(token));
            StockEntity stockEntity = getStockByCompanyName(request.getCompanyName());

            // Create or update owned stock
            Optional<OwnedStockEntity> ownedStock = ownedStockRepository.findByChildUserAndStock(childUserEntity, stockEntity);
            Map<String, Object> response = new HashMap<>();

            if (ownedStock.isPresent()) {
                updateOwnedStock(ownedStock.get(), request, childUserEntity, stockEntity);
                response.put("message", "Stock updated successfully");
            } else {
                createOwnedStock(childUserEntity, request, stockEntity);
                response.put("message", "Stock added successfully");
            }

            // Create response
            response.put("childUsername", childUserEntity.getUsername());
            response.put("newBalance", childUserEntity.getBalance());
            response.put("companyName", stockEntity.getCompanyName());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    public ResponseEntity<Map<String, Object>> getStocks(String token) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get all owned stocks
            List<OwnedStockEntity> ownedStocks = childUserRepository.findByUsername(username).get().getOwnedStocks();

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("ownedStocks", ownedStocks);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> getAvailableStocks(String token) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get all stocks
            List<StockEntity> availableStocks = stockRepository.findAll();

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("availableStocks", availableStocks);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    public MappingJacksonValue getAvailableIslands(String token) {
        // Get child entity
        String username = jwtUtil.getUsernameFromToken(token);
        // Checks if user from token exists
        childUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

        // Get all islands
        List<RoadmapIslandEntity> availableIslands = roadmapIslandRepository.findAll();

        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("availableIslands", availableIslands);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(response);
        mappingJacksonValue.setSerializationView(Views.NameOnly.class);
        return mappingJacksonValue;
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
            response.put("avatarId", childUserEntity.getAvatarId());
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

    public ResponseEntity<Map<String, Object>> getAchievements(String token) {
        try {
            // Get child entity
            String username = jwtUtil.getUsernameFromToken(token);
            // Checks if user from token exists
            childUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found"));

            // Get achievements progress
            List<AchievementProgressEntity> achievements = achievementProgressRepository
                    .findByChildUser(childUserRepository.findByUsername(username).get());

            // Get all achievements
            List<AchievementEntity> allAchievements = achievementRepository.findAll();

            // Filter and mark completed achievements, putting completed achievements and uncompleted achievements in separate lists
            List<AchievementEntity> completedAchievements = new ArrayList<>();
            List<AchievementEntity> uncompletedAchievements = new ArrayList<>();

            allAchievements.stream().forEach(achievement -> {
                if (achievements.stream().anyMatch(progress -> progress.getAchievement().getId().equals(achievement.getId()))) {
                    completedAchievements.add(achievement);
                } else {
                    uncompletedAchievements.add(achievement);
                }
            });

            Map<String, Object> response = new HashMap<>();
            response.put("completed", completedAchievements);
            response.put("uncompleted", uncompletedAchievements);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }

    }

    public ResponseEntity<Map<String, Object>> deposit(Long childId, AmountRequest request) {
        Double amount = request.getAmount();
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        ChildUserEntity childUser = childUserRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child user not found."));

        Double newBalance = childUser.getBalance() + amount;
        childUser.setBalance(newBalance);
        childUserRepository.save(childUser);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit successful");
        response.put("childId", childId);
        response.put("newBalance", newBalance);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> withdraw(Long childId, AmountRequest request) {
        Double amount = request.getAmount();
        Map<String, Object> response = new HashMap<>();
        try {
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
            }

            ChildUserEntity childUser = childUserRepository.findById(childId)
                    .orElseThrow(() -> new IllegalArgumentException("Child user not found."));

            if (childUser.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient balance for withdrawal.");
            }

            Double newBalance = childUser.getBalance() - amount;
            childUser.setBalance(newBalance);
            childUserRepository.save(childUser);

            response.put("message", "Withdrawal successful");
            response.put("childId", childId);
            response.put("newBalance", newBalance);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("message", "An unexpected error occurred");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper functions

    private void validateStockTransactionRequest(StockTransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name must be provided");
        }
        if (request.getStopLoss() == null || request.getStopLoss() < 0 || request.getStopLoss() > 100) {
            throw new IllegalArgumentException("Stop loss must be between 0 and 100");
        }
        if (request.getTakeProfit() == null || request.getTakeProfit() < 0 || request.getTakeProfit() > 100) {
            throw new IllegalArgumentException("Take profit must be between 0 and 100");
        }
    }

    private void ensureNonNegative(Double value, String fieldName) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
    }

    private void checkSufficientBalance(ChildUserEntity childUserEntity, Double requiredAmount) {
        Double balance = childUserEntity.getBalance() != null ? childUserEntity.getBalance() : 0.0;
        if (balance < requiredAmount) {
            throw new IllegalArgumentException("Insufficient balance for this transaction");
        }
    }

    @Transactional
    private void updateOwnedStock(OwnedStockEntity ownedStockEntity, StockTransactionRequest request, ChildUserEntity childUserEntity, StockEntity stockEntity) {
        Double newAmount = ownedStockEntity.getAmount() + request.getAmountChangeInCash();
        ensureNonNegative(newAmount, "Stock amount");

        if (newAmount == 0) {
            ownedStockRepository.delete(ownedStockEntity);
        } else {
            checkSufficientBalance(childUserEntity, request.getAmountChangeInCash());
            // Deduct from child user
            Double newBalance = childUserEntity.getBalance() - request.getAmountChangeInCash();
            childUserEntity.setBalance(newBalance);
            childUserRepository.save(childUserEntity);

            ownedStockEntity.setAmount(newAmount);
            ownedStockEntity.setAmountOfStocks(ownedStockEntity.getAmount() / stockEntity.getStockPrice());
            ownedStockEntity.setStopLoss(request.getStopLoss());
            ownedStockEntity.setTakeProfit(request.getTakeProfit());
            ownedStockRepository.save(ownedStockEntity);
        }
    }

    @Transactional
    private void createOwnedStock(ChildUserEntity childUserEntity, StockTransactionRequest request, StockEntity stockEntity) {
        OwnedStockEntity ownedStockEntity = new OwnedStockEntity(childUserEntity, stockEntity, request.getAmountChangeInCash(), request.getAmountChangeInCash() / stockEntity.getStockPrice());
        ensureNonNegative(ownedStockEntity.getAmount(), "Stock amount");

        checkSufficientBalance(childUserEntity, request.getAmountChangeInCash());
        // Deduct from child user
        Double newBalance = childUserEntity.getBalance() - request.getAmountChangeInCash();
        childUserEntity.setBalance(newBalance);
        childUserRepository.save(childUserEntity);

        ownedStockEntity.setStopLoss(request.getStopLoss());
        ownedStockEntity.setTakeProfit(request.getTakeProfit());
        ownedStockRepository.save(ownedStockEntity);
    }

    private ChildUserEntity getChildUserByUsername(String username) {
        return childUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Child user not found"));
    }

    private StockEntity getStockByCompanyName(String companyName) {
        return stockRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
    }

}
