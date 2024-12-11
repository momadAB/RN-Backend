package com.example.finquest.repository;

import com.example.finquest.entity.ParentUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentUserRepository extends JpaRepository<ParentUserEntity, Long> {
    Optional<ParentUserEntity> findByUsername(String username);
}
