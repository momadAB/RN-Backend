package com.example.finquest.repository;
import com.example.finquest.entity.RoadmapPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapPageRepository extends JpaRepository<RoadmapPageEntity, Long> {
}
