package com.example.finquest.services;

import com.example.finquest.bo.friendship.AddFriendRequest;
import com.example.finquest.bo.friendship.SendMessageRequest;
import com.example.finquest.config.JWTUtil;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.friendship.ChatEntity;
import com.example.finquest.entity.friendship.FriendshipEntity;
import com.example.finquest.entity.friendship.MessageEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.friendship.ChatRepository;
import com.example.finquest.repository.friendship.FriendshipRepository;
import com.example.finquest.repository.friendship.MessageRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public ResponseEntity<Map<String, Object>> sendMessage(String token, SendMessageRequest request) {
        // Get child user from token
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<ChildUserEntity> childUserOpt = childUserRepository.findByUsername(username);
        if (childUserOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token: User not found"));
        }
        ChildUserEntity childUser = childUserOpt.get();

        // Get friend user
        Optional<ChildUserEntity> friendOpt = childUserRepository.findByUsername(request.getUsername());
        if (friendOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Friend user not found"));
        }
        ChildUserEntity friendUser = friendOpt.get();

        // Check if chat already exists between the two users
        Optional<ChatEntity> chatOpt = chatRepository.findByChildUserAndFriendUser(childUser, friendUser);
        ChatEntity chat;
        if (chatOpt.isPresent()) {
            chat = chatOpt.get();
        } else {
            // If chat does not exist, create and save it
            chat = new ChatEntity();
            chat.setChildUser(childUser);
            chat.setFriendUser(friendUser);
            chat = chatRepository.save(chat);
        }

        // Create a new message
        MessageEntity message = new MessageEntity();
        message.setContent(request.getMessage());
        message.setChat(chat);
        message.setSender(childUser);
        messageRepository.save(message);

        // Save message into chat
        chat.getMessages().add(message);
        chatRepository.save(chat);

        return ResponseEntity.ok(Map.of("message", "Message sent successfully!"));
    }

    public ResponseEntity<Map<String, Object>> getChats(String token) {
        // Get child user from token
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<ChildUserEntity> childUserOpt = childUserRepository.findByUsername(username);
        if (childUserOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token: User not found"));
        }
        ChildUserEntity childUser = childUserOpt.get();

        // Get all chats for the child user
        List<ChatEntity> chats = chatRepository.findByChildUser(childUser);

        return ResponseEntity.ok(Map.of("chats", chats));
    }

}
