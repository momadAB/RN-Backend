package com.example.finquest.repository;
import com.example.finquest.entity.AchievementEntity;
import com.example.finquest.entity.OwnedStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStockEntity, Long>{
}
