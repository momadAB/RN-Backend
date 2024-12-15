package com.example.finquest.entity;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"child_user_id", "achievement_id"}
        )
)
public class AchievementProgressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // isCompleted
    @Column(nullable = false)
    private Boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUser;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private AchievementEntity achievement;


}
