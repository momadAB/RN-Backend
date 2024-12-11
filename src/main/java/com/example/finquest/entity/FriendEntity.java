package com.example.finquest.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friend_entity", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"child_user_id", "nickname"})
})
public class FriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUser;
}
