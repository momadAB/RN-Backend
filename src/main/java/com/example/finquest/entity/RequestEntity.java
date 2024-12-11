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
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long childId;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUser;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Boolean isRejected;

    @Column(nullable = false)
    private Boolean isComplete;

}
