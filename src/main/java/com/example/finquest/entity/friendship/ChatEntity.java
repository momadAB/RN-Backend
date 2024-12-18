package com.example.finquest.entity.friendship;

import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.view.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.IdOnly.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonView(Views.IdOnly.class)
    private ChildUserEntity childUser;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    @JsonView(Views.IdOnly.class)
    private ChildUserEntity friendUser;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonView(Views.IdOnly.class)
    private List<MessageEntity> messages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ChildUserEntity getChildUser() {
        return childUser;
    }

    public void setChildUser(ChildUserEntity childUser) {
        this.childUser = childUser;
    }

    public ChildUserEntity getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(ChildUserEntity friendUser) {
        this.friendUser = friendUser;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }

    public void addMessage(MessageEntity message) {
        messages.add(message);
    }
}
