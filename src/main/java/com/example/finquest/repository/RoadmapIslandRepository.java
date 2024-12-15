package com.example.finquest.repository;
import com.example.finquest.entity.RoadmapIslandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadmapIslandRepository extends JpaRepository<RoadmapIslandEntity, Long> {
}
