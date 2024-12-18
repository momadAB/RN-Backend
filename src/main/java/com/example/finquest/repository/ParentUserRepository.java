package com.example.finquest.repository;

import com.example.finquest.entity.ParentUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentUserRepository extends JpaRepository<ParentUserEntity, Long> {
    // Use lower() in the query for case-insensitivity
    @Query("SELECT p FROM ParentUserEntity p WHERE LOWER(p.username) = LOWER(:username)")
    Optional<ParentUserEntity> findByUsername(@Param("username") String username);
}
