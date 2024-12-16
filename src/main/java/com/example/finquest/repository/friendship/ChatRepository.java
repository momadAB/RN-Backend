package com.example.finquest.repository.friendship;

import com.example.finquest.entity.AchievementProgressEntity;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.ChatEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    Optional<ChatEntity> findByChildUserAndFriendUser(ChildUserEntity childUserEntity, ChildUserEntity childUserEntity1);

    List<ChatEntity> findByChildUser(ChildUserEntity childUser);
}
