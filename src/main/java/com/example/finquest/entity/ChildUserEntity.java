package com.example.finquest.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChildUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long achievementPoints = 0L;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long avatarId;

    @ManyToOne
    @JoinColumn(name = "parent_user_id", nullable = false)
    private ParentUserEntity parentUser;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "completed_steps", joinColumns = @JoinColumn(name = "child_user_id"))
    @Column(name = "step_id")
    private List<Long> completedStepsOfRoadmap;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RequestEntity> madeRequests;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AchievementEntity> achievements;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FriendEntity> friends;

    @Column(nullable = false)
    private boolean isAllowedToMakeTransactionsWithNoPermission;
}
