package com.example.finquest.repository;
import com.example.finquest.entity.RoadmapIslandEntity;
import com.example.finquest.entity.RoadmapLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapLessonRepository extends JpaRepository<RoadmapLessonEntity, Long> {
    List<RoadmapLessonEntity> findByIsland_Id(Long islandId);
}
