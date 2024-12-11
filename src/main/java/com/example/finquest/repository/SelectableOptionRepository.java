package com.example.finquest.repository;
import com.example.finquest.entity.SelectableOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectableOptionRepository extends JpaRepository<SelectableOptionEntity, Long> {
}
