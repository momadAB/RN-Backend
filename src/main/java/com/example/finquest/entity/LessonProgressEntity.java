package com.example.finquest.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;

@Entity
public class LessonProgressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUser;

    @ManyToOne
    @JoinColumn(name = "roadmap_page_id", nullable = false)
    private RoadmapPageEntity roadmapPage;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    public Long getId() {
        return id;
    }

    public ChildUserEntity getChildUser() {
        return childUser;
    }

    public void setChildUser(ChildUserEntity childUser) {
        this.childUser = childUser;
    }

    public RoadmapPageEntity getRoadmapPage() {
        return roadmapPage;
    }

    public void setRoadmapPage(RoadmapPageEntity roadmapPage) {
        this.roadmapPage = roadmapPage;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
