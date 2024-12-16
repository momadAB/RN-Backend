package com.example.finquest.bo;

public class ApprovalRequest {
    private boolean isAllowedToMakeTransactionsWithNoPermission;

    public boolean getIsAllowedToMakeTransactionsWithNoPermission() {
        return isAllowedToMakeTransactionsWithNoPermission;
    }

    public void setAllowedToMakeTransactionsWithNoPermission(boolean allowedToMakeTransactionsWithNoPermission) {
        isAllowedToMakeTransactionsWithNoPermission = allowedToMakeTransactionsWithNoPermission;
    }
}
