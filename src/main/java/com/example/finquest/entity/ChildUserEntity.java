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

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RequestEntity> madeRequests;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AchievementEntity> achievements;

    @ManyToMany
    @JoinTable(
            name = "child_user_friends",
            joinColumns = @JoinColumn(name = "child_user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<ChildUserEntity> friendsList;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OwnedStockEntity> ownedStocks;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LessonProgressEntity> progressEntities;

    @Column(nullable = false)
    private boolean isAllowedToMakeTransactionsWithNoPermission;

    public List<OwnedStockEntity> getOwnedStocks() {
        return ownedStocks;
    }

    public void setOwnedStocks(List<OwnedStockEntity> ownedStocks) {
        this.ownedStocks = ownedStocks;
    }

    public List<LessonProgressEntity> getProgressEntities() {
        return progressEntities;
    }

    public void setProgressEntities(List<LessonProgressEntity> progressEntities) {
        this.progressEntities = progressEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
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

    public List<ChildUserEntity> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<ChildUserEntity> friendsList) {
        this.friendsList = friendsList;
    }

    public boolean isAllowedToMakeTransactionsWithNoPermission() {
        return isAllowedToMakeTransactionsWithNoPermission;
    }

    public void setAllowedToMakeTransactionsWithNoPermission(boolean allowedToMakeTransactionsWithNoPermission) {
        isAllowedToMakeTransactionsWithNoPermission = allowedToMakeTransactionsWithNoPermission;
    }
}
