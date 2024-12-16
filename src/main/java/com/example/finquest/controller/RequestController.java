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

    @GetMapping("/get/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable Long id) {
        RequestResponse requestResponse = requestService.getRequestById(id);
        return new ResponseEntity<>(requestResponse, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<RequestEntity>> getAllRequests() {
        List<RequestEntity> requests = requestService.getAllRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRequestById(@PathVariable Long id) {
        try {
            requestService.deleteRequestById(id);
            String message = "Request with ID " + id + " has been deleted";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody RequestRequest requestRequest) {

        try {
            // Call the service to update the request status
            ResponseEntity<RequestResponse> updatedRequest = requestService.updateRequestStatus(
                    id,
                    requestRequest.getIsComplete(),
                    requestRequest.getIsRejected()
            );
            return updatedRequest;
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }




}
