package com.example.finquest.repository;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.LessonProgressEntity;
import com.example.finquest.entity.RoadmapLessonEntity;
import com.example.finquest.entity.RoadmapPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, Long>{
    Optional<LessonProgressEntity> findByChildUserAndRoadmapLesson(ChildUserEntity childUserEntity, RoadmapLessonEntity lessonEntity);
}
