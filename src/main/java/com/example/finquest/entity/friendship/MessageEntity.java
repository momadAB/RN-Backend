package com.example.finquest.entity.friendship;

import com.example.finquest.entity.ChildUserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private ChildUserEntity sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Date sentAt = new Date();

    public Long getId() {
        return id;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public ChildUserEntity getSender() {
        return sender;
    }

    public void setSender(ChildUserEntity sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
