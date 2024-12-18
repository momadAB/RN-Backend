package com.example.finquest.repository.friendship;

import com.example.finquest.entity.AchievementProgressEntity;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.ChatEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    Optional<ChatEntity> findByChildUserAndFriendUser(ChildUserEntity childUserEntity, ChildUserEntity childUserEntity1);

    List<ChatEntity> findByChildUser(ChildUserEntity childUser);

    @Query("SELECT c FROM ChatEntity c WHERE (c.childUser = :user1 AND c.friendUser = :user2) OR (c.childUser = :user2 AND c.friendUser = :user1)")
    Optional<ChatEntity> findByUsers(@Param("user1") ChildUserEntity user1, @Param("user2") ChildUserEntity user2);

    @Query("SELECT c FROM ChatEntity c WHERE c.childUser = :user OR c.friendUser = :user")
    List<ChatEntity> findByUser(@Param("user") ChildUserEntity user);
}
