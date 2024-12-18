package com.example.finquest.bo;

import com.example.finquest.entity.*;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildUserResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("achievementPoints")
    private Long achievementPoints;
    @JsonProperty("username")
    private String username;
    @JsonProperty("avatarId")
    private Long avatarId;
    @JsonProperty("roles")
    private String roles = "ROLE_CHILD";
    @JsonProperty("madeRequests")
    private List<RequestEntity> madeRequests;
    @JsonProperty("achievementProgress")
    private List<AchievementProgressEntity> achievementProgress;
    @JsonProperty("friendsList")
    private List<FriendshipEntity> friendsList;
    @JsonProperty("ownedStocks")
    private List<OwnedStockEntity> ownedStocks;
    @JsonProperty("progressEntities")
    private List<LessonProgressEntity> progressEntities;
    @JsonProperty("isAllowedToMakeTransactionsWithNoPermission")
    private boolean isAllowedToMakeTransactionsWithNoPermission;


    public ChildUserResponse(ChildUserEntity child) {
        this.id = child.getId();
        this.balance = child.getBalance();
        this.achievementPoints = child.getAchievementPoints();
        this.username = child.getUsername();
        this.avatarId = child.getAvatarId();
        this.roles = child.getRoles();
        this.madeRequests = child.getMadeRequests();
        this.achievementProgress = child.getAchievementProgress();
        this.friendsList = child.getFriendships();
        this.ownedStocks = child.getOwnedStocks();
        this.progressEntities = child.getProgressEntities();
        this.isAllowedToMakeTransactionsWithNoPermission = child.isAllowedToMakeTransactionsWithNoPermission();


    }


}
