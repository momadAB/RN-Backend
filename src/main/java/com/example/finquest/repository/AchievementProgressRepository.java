package com.example.finquest.repository;

import com.example.finquest.entity.AchievementProgressEntity;
import com.example.finquest.entity.ChildUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementProgressRepository extends JpaRepository<AchievementProgressEntity, Long> {
    List<AchievementProgressEntity> findByChildUser(ChildUserEntity childUserEntity);
}
