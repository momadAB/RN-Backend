package com.example.finquest.controller;

import com.example.finquest.bo.RequestRequest;
import com.example.finquest.bo.RequestResponse;
import com.example.finquest.entity.RequestEntity;
import com.example.finquest.services.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // Add a new request
    @PostMapping("/add")
    public ResponseEntity<RequestResponse> addRequest(@RequestBody RequestRequest requestRequest) {
        RequestResponse requestResponse = requestService.addRequest(requestRequest);
        return new ResponseEntity<>(requestResponse, HttpStatus.CREATED);
    }

//    // Get a request by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<RequestResponse> getRequestById(@PathVariable Long id) {
//        RequestResponse request = requestService.getRequestById(id);
//        return new ResponseEntity<>(request, HttpStatus.OK);
//    }
//
//    // Update request status
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<RequestResponse> updateRequestStatus(
//            @PathVariable Long id,
//            @RequestParam Boolean isRejected,
//            @RequestParam Boolean isComplete) {
//        RequestResponse updatedRequest = requestService.updateRequestStatus(id, isRejected, isComplete);
//        return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
//    }
//
//    // Get all requests
//    @GetMapping
//    public ResponseEntity<List<RequestResponse>> getAllRequests() {
//        List<RequestResponse> requests = requestService.getAllRequests();
//        return new ResponseEntity<>(requests, HttpStatus.OK);
//    }
//
//    // Delete a request by ID
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRequestById(@PathVariable Long id) {
//        requestService.deleteRequestById(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
