package com.example.finquest.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SelectableOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column
    private String hiddenDetail;

    @ManyToOne
    @JoinColumn(name = "roadmap_page_id", nullable = false)
    @JsonBackReference
    private RoadmapPageEntity roadmapPage;

    @Column(nullable = false)
    private String notificationText;

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

    public String getHiddenDetail() {
        return hiddenDetail;
    }

    public void setHiddenDetail(String hiddenDetail) {
        this.hiddenDetail = hiddenDetail;
    }

    public RoadmapPageEntity getRoadmapPage() {
        return roadmapPage;
    }

    public void setRoadmapPage(RoadmapPageEntity roadmapPage) {
        this.roadmapPage = roadmapPage;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
}
