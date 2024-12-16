package com.example.finquest.services;

import com.example.finquest.bo.RequestRequest;
import com.example.finquest.bo.RequestResponse;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.RequestEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.RequestRepository;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new IllegalArgumentException("Child with ID " + requestRequest.getChildId() + " not found"));

        RequestEntity requestEntity = new RequestEntity(
                requestRequest.getChildId(),
                requestRequest.getDescription(),
                childUser,
                requestRequest.getAmount(),
                requestRequest.getIsRejected(),
                requestRequest.getIsComplete()
        );

        RequestEntity savedRequest = requestRepository.save(requestEntity);

        // Explicitly print out to confirm getter works
        System.out.println("Is Complete: " + savedRequest.getIsComplete());

        return new RequestResponse(
                savedRequest.getId(),
                savedRequest.getChildId(),
                savedRequest.getDescription(),
                savedRequest.getAmount(),
                savedRequest.getIsRejected(),
                savedRequest.getIsComplete()  // This should now work if Lombok is working properly
        );
    }



}
