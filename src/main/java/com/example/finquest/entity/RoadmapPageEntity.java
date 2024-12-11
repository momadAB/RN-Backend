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
    @JoinColumn(name = "roadmap_island_id", nullable = false)
    private RoadmapIslandEntity island;

    @OneToMany(mappedBy = "roadmapPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectableOptionEntity> options;
}
