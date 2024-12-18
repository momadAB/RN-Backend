package com.example.finquest.repository;
import com.example.finquest.entity.ChildUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChildUserRepository extends JpaRepository<ChildUserEntity, Long> {
    // Use lower() in the query for case-insensitivity
    @Query("SELECT c FROM ChildUserEntity c WHERE LOWER(c.username) = LOWER(:username)")
    Optional<ChildUserEntity> findByUsername(@Param("username") String username);
}
