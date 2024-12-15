package com.example.finquest.controller;

import com.example.finquest.bo.StockTransactionRequest;
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

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a child endpoint!";
    }

    @PostMapping("/stock-transaction")
    public ResponseEntity<Map<String, String>> makeStockTransaction(@RequestBody StockTransactionRequest request, @RequestHeader("Authorization") String token) {
        return childUserService.makeStockTransaction(request, token);
    }
}
