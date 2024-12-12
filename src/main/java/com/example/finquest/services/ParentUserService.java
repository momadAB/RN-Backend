package com.example.finquest.services;

import com.example.finquest.bo.ParentUserResponse;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentUserService {

    private final ParentUserRepository parentUserRepository;

    public ParentUserResponse getParentUserById(Long id) {
        ParentUserEntity parentUserEntity = parentUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent with ID " + id + " not found"));
        ParentUserResponse responseId = new ParentUserResponse(parentUserEntity);
        return responseId;
    }

}
