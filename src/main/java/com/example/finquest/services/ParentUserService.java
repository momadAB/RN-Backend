package com.example.finquest.services;

import com.example.finquest.bo.RegisterChildUserRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.ParentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ParentUserService {
}
