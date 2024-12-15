package com.example.finquest.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoadmapPageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "roadmap_lesson_id", nullable = false)
    private RoadmapLessonEntity lesson;

    @OneToMany(mappedBy = "roadmapPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectableOptionEntity> options;
}
