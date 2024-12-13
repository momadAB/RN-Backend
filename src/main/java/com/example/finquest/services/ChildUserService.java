package com.example.finquest.services;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.StockTransactionRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.OwnedStockEntity;
import com.example.finquest.entity.StockEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.OwnedStockRepository;
import com.example.finquest.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ChildUserService {

    private final ChildUserRepository childUserRepository;
    private final OwnedStockRepository ownedStockRepository;
    private final StockRepository stockRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public ChildUserService(ChildUserRepository childUserRepository, OwnedStockRepository ownedStockRepository, StockRepository stockRepository, JWTUtil jwtUtil) {
        this.childUserRepository = childUserRepository;
        this.ownedStockRepository = ownedStockRepository;
        this.stockRepository = stockRepository;
        this.jwtUtil = jwtUtil;
    }

    public ChildUserResponse getChildUserById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Child with ID " + id + " not found"));
        ChildUserResponse childUserResponse = new ChildUserResponse(childUserEntity);
        return childUserResponse;
    }

    public ResponseEntity<Map<String, String>> makeStockTransaction(StockTransactionRequest request, String token) {
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

            // If child owns the stock
            if (ownedStock.isPresent()) {
                // Update the existing stock amount
                OwnedStockEntity ownedStockEntity = ownedStock.get();
                Double newAmount = ownedStockEntity.getAmount() + request.getAmountChange();

                // Check if newAmount is less than 0
                if (newAmount < 0) {
                    throw new IllegalArgumentException("Stock amount cannot be negative");
                }

                // If newAmount is 0, delete the stock
                if (newAmount == 0) {
                    ownedStockRepository.delete(ownedStockEntity);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Stock deleted successfully");
                    return ResponseEntity.ok(response);
                }

                // Check child balance and deduct if necessary
                // Defaults balance to 0 if it's null
                Double balance = childUserEntity.getBalance() != null ? childUserEntity.getBalance() : 0.0;
                if (balance < request.getAmountChange()) {
                    throw new IllegalArgumentException("Insufficient balance for this transaction");
                }

                childUserEntity.setBalance(balance - request.getAmountChange());
                childUserRepository.save(childUserEntity);

                // Update the stock and save changes
                ownedStockEntity.setAmount(newAmount);
                ownedStockRepository.save(ownedStockEntity);

                // Return response
                Map<String, String> response = new HashMap<>();
                response.put("message", "Stock updated successfully");
                response.put("new balance", String.valueOf(childUserEntity.getBalance()));
                response.put("stock", ownedStockEntity.toString());
                return ResponseEntity.ok(response);
            } else {
                // If stock is not owned, create a new stock entry
                OwnedStockEntity ownedStockEntity = new OwnedStockEntity(childUserEntity, stockEntity, request.getAmountChange());

                // Check if amount is less than 0
                if (ownedStockEntity.getAmount() < 0) {
                    throw new IllegalArgumentException("Stock amount cannot be negative");
                }

                // Check child balance and deduct
                Double balance = childUserEntity.getBalance() != null ? childUserEntity.getBalance() : 0.0;
                if (balance < request.getAmountChange()) {
                    throw new IllegalArgumentException("Insufficient balance for this transaction");
                }

                childUserEntity.setBalance(balance - request.getAmountChange());
                childUserRepository.save(childUserEntity);

                // Save the new stock
                ownedStockRepository.save(ownedStockEntity);

                // Return response
                Map<String, String> response = new HashMap<>();
                response.put("message", "Stock added successfully");
                response.put("new balance", String.valueOf(childUserEntity.getBalance()));
                response.put("stock", ownedStockEntity.toString());
                return ResponseEntity.ok(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
