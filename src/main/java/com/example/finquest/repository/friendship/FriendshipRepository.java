package com.example.finquest.repository.friendship;

import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    Object findByChildUser(ChildUserEntity childUserEntity);

    Optional<FriendshipEntity> findByChildUserAndFriendUser(ChildUserEntity childUserEntity, ChildUserEntity childUserEntity1);
}
