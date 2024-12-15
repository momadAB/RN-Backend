package com.example.finquest.repository;
import com.example.finquest.entity.AchievementEntity;
import com.example.finquest.entity.OwnedStockEntity;
import com.example.finquest.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long>{
    Optional<StockEntity> findByCompanyName(String companyName);
}
