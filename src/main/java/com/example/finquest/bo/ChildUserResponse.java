package com.example.finquest.bo;

import com.example.finquest.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildUserResponse {

    private Long id;
    private Double balance;
    private Long achievementPoints;
    private String name;
    private Long avatarId;
    private String roles = "ROLE_CHILD";
    private ParentUserEntity parentUser;
    private List<Long> completedStepsOfRoadmap;
    private List<RequestEntity> madeRequests;
    private List<AchievementEntity> achievements;
//    private List<FriendEntity> friends;
    private boolean isAllowedToMakeTransactionsWithNoPermission;

    public ChildUserResponse(ChildUserEntity childUserEntity) {
    }
}
