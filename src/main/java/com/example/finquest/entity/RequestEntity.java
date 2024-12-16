package com.example.finquest.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.spi.ToolProvider;

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
    @JsonIgnore
    private Long childId;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    @JsonIgnore
    private ChildUserEntity childUser;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Boolean isRejected;

    @Column(nullable = false)
    private Boolean isComplete;

    public RequestEntity() {}

    public RequestEntity(Long childId, String description, ChildUserEntity childUser, Double amount, Boolean isRejected, Boolean isComplete) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChildUserEntity getChildUser() {
        return childUser;
    }

    public void setChildUser(ChildUserEntity childUser) {
        this.childUser = childUser;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getIsRejected() {
        return isRejected;
    }

    public void setIsRejected(Boolean rejected) {
        isRejected = rejected;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean complete) {
        isComplete = complete;
    }
}
