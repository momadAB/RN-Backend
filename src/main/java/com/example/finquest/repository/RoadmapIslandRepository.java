package com.example.finquest.repository;
import com.example.finquest.entity.RoadmapIslandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapIslandRepository extends JpaRepository<RoadmapIslandEntity, Long> {
}
