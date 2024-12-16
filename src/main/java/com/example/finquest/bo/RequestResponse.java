package com.example.finquest.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class RequestResponse {
    private Long id;
    private Long childId;
    private String description;
    private Double amount;
    private Boolean isRejected;
    private Boolean isComplete;



    public RequestResponse(Long id, Long childId, String description, Double amount, Boolean isRejected, Boolean isComplete) {
        this.id = id;
        this.childId = childId;
        this.description = description;
        this.amount = amount;
        this.isRejected = isRejected;
        this.isComplete = isComplete;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getRejected() {
        return isRejected;
    }

    public void setRejected(Boolean rejected) {
        isRejected = rejected;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }
}
