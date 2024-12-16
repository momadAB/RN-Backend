package com.example.finquest.entity.friendship;

import com.example.finquest.entity.ChildUserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ChildUserEntity childuser;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private ChildUserEntity frienduser;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ChildUserEntity getChilduser() {
        return childuser;
    }

    public void setChilduser(ChildUserEntity childuser) {
        this.childuser = childuser;
    }

    public ChildUserEntity getFrienduser() {
        return frienduser;
    }

    public void setFrienduser(ChildUserEntity frienduser) {
        this.frienduser = frienduser;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }
}
