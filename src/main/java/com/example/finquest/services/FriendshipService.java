package com.example.finquest.services;

import com.example.finquest.bo.friendship.AddFriendRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.friendship.ChatRepository;
import com.example.finquest.repository.friendship.FriendshipRepository;
import com.example.finquest.repository.friendship.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FriendshipService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChildUserRepository childUserRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private JWTUtil jwtUtil;


    public ResponseEntity<Map<String, Object>> addFriend(String token, AddFriendRequest request) {
        // Get child from token
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<ChildUserEntity> childUser = childUserRepository.findByUsername(username);

        // Check that friend exists
        Optional<ChildUserEntity> friend = childUserRepository.findByUsername(request.getUsername());
        if (friend.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        // Check that friendship doesnt already exist
        if (friendshipRepository.findByChildUserAndFriendUser(childUser.get(), childUserRepository.findByUsername(request.getUsername()).get()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Friendship already exists"));
        }

        // Add friend
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setChildUser(childUser.get());
        friendship.setFriendUser(childUserRepository.findByUsername(request.getUsername()).get());
        friendshipRepository.save(friendship);

        // Add friend for other user as well
        friendship = new FriendshipEntity();
        friendship.setChildUser(childUserRepository.findByUsername(request.getUsername()).get());
        friendship.setFriendUser(childUser.get());
        friendshipRepository.save(friendship);

        // Response
        return ResponseEntity.ok(Map.of("message", "Friend added successfully!"));
    }

    public ResponseEntity<Map<String, Object>> getFriends(String token) {
        // Get child from token
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<ChildUserEntity> childUser = childUserRepository.findByUsername(username);

        // Return usernames of friends
        List<FriendshipEntity> friendshipEntityList = childUser.get().getFriendships();

        return ResponseEntity.ok(Map.of("friends", friendshipEntityList.stream().map(FriendshipEntity::getFriendUser).map(ChildUserEntity::getUsername).toList()));
    }
}
