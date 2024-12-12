package com.example.finquest.repository;
import com.example.finquest.entity.AchievementEntity;
import com.example.finquest.entity.OwnedStockEntity;
import com.example.finquest.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long>{
}
