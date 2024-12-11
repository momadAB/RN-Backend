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
public class ChildUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double balance;

    @Column
    private Long achievementPoints;

    @Column
    private String name;

    @Column
    private Long avatarId;

    @ElementCollection
    @CollectionTable(name = "completed_steps", joinColumns = @JoinColumn(name = "child_user_id"))
    @Column(name = "step_id")
    private List<Long> completedStepsOfRoadmap;

//    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<RequestEntity> madeRequests;
//
//    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<AchievementEntity> achievements;
//
    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendEntity> friends;

    @Column
    private Boolean isAllowedToMakeTransactionsWithNoPermission;


}
