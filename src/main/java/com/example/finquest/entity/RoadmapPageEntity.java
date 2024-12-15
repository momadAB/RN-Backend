package com.example.finquest.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private RoadmapLessonEntity lesson;

    @OneToMany(mappedBy = "roadmapPage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SelectableOptionEntity> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RoadmapLessonEntity getLesson() {
        return lesson;
    }

    public void setLesson(RoadmapLessonEntity lesson) {
        this.lesson = lesson;
    }

    public List<SelectableOptionEntity> getOptions() {
        return options;
    }

    public void setOptions(List<SelectableOptionEntity> options) {
        this.options = options;
    }
}
