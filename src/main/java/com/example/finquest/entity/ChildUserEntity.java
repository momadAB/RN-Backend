package com.example.finquest.entity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class ChildUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Long achievementPoints = 0L;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false)
    private Long avatarId;

    @Column(nullable = false)
    private String roles = "ROLE_CHILD";

    @ManyToOne
    @JoinColumn(name = "parent_user_id", nullable = false)
    @JsonBackReference
    @JsonIgnoreProperties(value ={"childUser"} )
    private ParentUserEntity parentUser;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("childUser")
    private List<RequestEntity> madeRequests;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("childUser")
    private List<AchievementProgressEntity> achievementProgress;

    // Friendships
    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("childUser")
    @JsonManagedReference
    private List<FriendshipEntity> friendships;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("childUser")
    private List<OwnedStockEntity> ownedStocks;

    @OneToMany(mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("childUser")
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<AchievementProgressEntity> getAchievementProgress() {
        return achievementProgress;
    }

    public void setAchievementProgress(List<AchievementProgressEntity> achievementProgress) {
        this.achievementProgress = achievementProgress;
    }

    public List<FriendshipEntity> getFriendships() {
        return friendships;
    }

    public void setFriendships(List<FriendshipEntity> friendships) {
        this.friendships = friendships;
    }

    public boolean isAllowedToMakeTransactionsWithNoPermission() {
        return isAllowedToMakeTransactionsWithNoPermission;
    }

    public void setAllowedToMakeTransactionsWithNoPermission(boolean allowedToMakeTransactionsWithNoPermission) {
        isAllowedToMakeTransactionsWithNoPermission = allowedToMakeTransactionsWithNoPermission;
    }
}
