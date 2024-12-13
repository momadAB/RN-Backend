package com.example.finquest.repository;
import com.example.finquest.entity.AchievementEntity;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.OwnedStockEntity;
import com.example.finquest.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStockEntity, Long>{
    Optional<OwnedStockEntity> findByChildUserAndStock(ChildUserEntity childUserEntity, StockEntity stockEntity);
}
