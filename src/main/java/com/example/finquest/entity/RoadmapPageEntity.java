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

    @Column
    private String header1;
    @Column
    private String header2;
    @Column
    private String header3;

    @Column
    private String text1;
    @Column
    private String text2;
    @Column
    private String text3;

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getHeader3() {
        return header3;
    }

    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

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
