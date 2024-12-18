package com.example.finquest.entity;
import com.example.finquest.view.Views;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
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
public class RoadmapIslandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.NameOnly.class)
    private Long id;

    @Column(nullable = false)
    private String logoUrl;

    @Column(nullable = false)
    @JsonView(Views.NameOnly.class)
    private String title;

    @OneToMany(mappedBy = "island", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RoadmapLessonEntity> lessons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RoadmapLessonEntity> getLessons() {
        return lessons;
    }

    public void setLessons(List<RoadmapLessonEntity> lessons) {
        this.lessons = lessons;
    }
}
