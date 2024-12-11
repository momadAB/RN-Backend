package com.example.finquest.repository;
import com.example.finquest.entity.ChildUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChildUserRepository extends JpaRepository<ChildUserEntity, Long> {
}
