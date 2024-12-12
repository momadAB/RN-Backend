package com.example.finquest.repository;
import com.example.finquest.entity.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, Long>{
}
