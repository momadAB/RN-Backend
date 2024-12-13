package com.example.finquest.services;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChildUserService {

    private final ChildUserRepository childUserRepository;

    @Autowired
    public ChildUserService(ChildUserRepository childUserRepository) {
        this.childUserRepository = childUserRepository;
    }

    public ChildUserResponse getChildUserById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Child with ID " + id + " not found"));
        ChildUserResponse childUserResponse = new ChildUserResponse(childUserEntity);
        return childUserResponse;
    }
}
