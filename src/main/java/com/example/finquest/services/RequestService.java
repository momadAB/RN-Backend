package com.example.finquest.services;

import com.example.finquest.bo.RequestRequest;
import com.example.finquest.bo.RequestResponse;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.RequestEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.RequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final ChildUserRepository childUserRepository;

    public RequestService(RequestRepository requestRepository, ChildUserRepository childUserRepository) {
        this.requestRepository = requestRepository;
        this.childUserRepository = childUserRepository;
    }

    public RequestResponse addRequest(RequestRequest requestRequest) {
        ChildUserEntity childUser = childUserRepository.findById(requestRequest.getChildId())
                .orElseThrow(() -> new NoSuchElementException("Child with ID " + requestRequest.getChildId() + " not found"));

        RequestEntity requestEntity = new RequestEntity();

        requestEntity.setChildId(requestRequest.getChildId());
        requestEntity.setChildUser(childUser);
        requestEntity.setDescription(requestRequest.getDescription());
        requestEntity.setIsComplete(requestRequest.getIsComplete());
        requestEntity.setIsRejected(requestRequest.getIsRejected());
        requestEntity.setAmount(requestRequest.getAmount());

        RequestEntity savedRequest = requestRepository.save(requestEntity);

        return new RequestResponse(
                savedRequest.getId(),
                savedRequest.getChildId(),
                savedRequest.getDescription(),
                savedRequest.getAmount(),
                savedRequest.getIsRejected(),
                savedRequest.getIsComplete()
        );
    }

    public RequestResponse getRequestById(Long id) {
        RequestEntity requestEntity = requestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request with ID " + id + " not found"));
        return new RequestResponse(
                requestEntity.getId(),
                requestEntity.getChildId(),
                requestEntity.getDescription(),
                requestEntity.getAmount(),
                requestEntity.getIsRejected(),
                requestEntity.getIsComplete()
        );
    }

    public List<RequestEntity> getAllRequests() {
        return requestRepository.findAll();
    }

    public ResponseEntity<RequestResponse> updateRequestStatus(Long id, Boolean isComplete, Boolean isRejected) {
        RequestEntity requestEntity = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request with ID " + id + " not found"));

        try {
            if(isComplete == null || isRejected == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }

            requestEntity.setIsComplete(isComplete);
            requestEntity.setIsRejected(isRejected);

            RequestEntity updatedRequest = requestRepository.save(requestEntity);
            return ResponseEntity.ok( new RequestResponse(
                    updatedRequest.getId(),
                    updatedRequest.getChildId(),
                    updatedRequest.getDescription(),
                    updatedRequest.getAmount(),
                    updatedRequest.getIsRejected(),
                    updatedRequest.getIsComplete()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }

    public void deleteRequestById(Long id) {
        RequestEntity requestEntity = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request with ID " + id + " not found"));
        requestRepository.delete(requestEntity);
    }


}
