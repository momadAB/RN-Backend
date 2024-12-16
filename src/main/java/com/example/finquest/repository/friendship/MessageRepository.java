package com.example.finquest.repository.friendship;

import com.example.finquest.entity.AchievementProgressEntity;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.example.finquest.entity.friendship.MessageEntity;
import org.aspectj.bridge.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
