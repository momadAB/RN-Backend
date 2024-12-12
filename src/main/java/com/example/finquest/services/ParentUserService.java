package com.example.finquest.services;
import com.example.finquest.repository.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentUserService {

    private final ParentUserRepository parentUserRepository;


}
