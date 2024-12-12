package com.example.finquest.services;

import com.example.finquest.bo.ChildUserResponse;
import com.example.finquest.bo.ParentUserResponse;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildUserService {

    private final ChildUserRepository childUserRepository;

    public ChildUserResponse getChildUserById(Long id) {
        ChildUserEntity childUserEntity = childUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child with ID " + id + " not found"));
        ChildUserResponse childUserResponse = new ChildUserResponse(childUserEntity);
        return childUserResponse;
    }


}
