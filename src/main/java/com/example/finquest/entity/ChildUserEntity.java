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
    private Double balance = 0.0;

    @Column(nullable = false)
    private Long achievementPoints = 0L;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false)
    private Long avatarId;

    @Column(nullable = false)
    private String roles = "ROLE_CHILD";

    @ManyToOne
    @JoinColumn(name = "parent_user_id", nullable = false)
    private ParentUserEntity parentUser;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "completed_steps", joinColumns = @JoinColumn(name = "child_user_id"))
    @Column(name = "step_id")
    private List<Long> completedStepsOfRoadmap;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RequestEntity> madeRequests;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AchievementEntity> achievements;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FriendEntity> friends;

    @Column(nullable = false)
    private boolean isAllowedToMakeTransactionsWithNoPermission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Long getAchievementPoints() {
        return achievementPoints;
    }

    public void setAchievementPoints(Long achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public ParentUserEntity getParentUser() {
        return parentUser;
    }

    public void setParentUser(ParentUserEntity parentUser) {
        this.parentUser = parentUser;
    }

    public List<Long> getCompletedStepsOfRoadmap() {
        return completedStepsOfRoadmap;
    }

    public void setCompletedStepsOfRoadmap(List<Long> completedStepsOfRoadmap) {
        this.completedStepsOfRoadmap = completedStepsOfRoadmap;
    }

    public List<RequestEntity> getMadeRequests() {
        return madeRequests;
    }

    public void setMadeRequests(List<RequestEntity> madeRequests) {
        this.madeRequests = madeRequests;
    }

    public List<AchievementEntity> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementEntity> achievements) {
        this.achievements = achievements;
    }

    public List<FriendEntity> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendEntity> friends) {
        this.friends = friends;
    }

    public boolean isAllowedToMakeTransactionsWithNoPermission() {
        return isAllowedToMakeTransactionsWithNoPermission;
    }

    public void setAllowedToMakeTransactionsWithNoPermission(boolean allowedToMakeTransactionsWithNoPermission) {
        isAllowedToMakeTransactionsWithNoPermission = allowedToMakeTransactionsWithNoPermission;
    }
}
