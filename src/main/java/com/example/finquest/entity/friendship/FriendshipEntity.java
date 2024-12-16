package com.example.finquest.entity.friendship;

import com.example.finquest.entity.ChildUserEntity;

import javax.persistence.Entity;
import javax.persistence.*;

@Entity
public class FriendshipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ChildUserEntity childUser;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private ChildUserEntity friendUser;

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
}
