package com.example.finquest.bo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class RequestRequest {
    private Long childId;
    private String description;
    private Double amount;
    private Boolean isRejected;
    private Boolean isComplete;

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
