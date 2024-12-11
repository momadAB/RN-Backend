package com.example.finquest.repository;
import com.example.finquest.entity.AchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, Long>{
}
