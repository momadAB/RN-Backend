package com.example.finquest.repository;

import com.example.finquest.entity.AchievementProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementProgressRepository extends JpaRepository<AchievementProgressEntity, Long> {
}
