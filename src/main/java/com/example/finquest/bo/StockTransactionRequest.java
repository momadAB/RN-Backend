package com.example.finquest.bo;

public class StockTransactionRequest {
    private String companyName;
    private Double amountChangeInCash;
    private Double stopLoss;
    private Double takeProfit;

    public Double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(Double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public Double getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(Double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getAmountChangeInCash() {
        return amountChangeInCash;
    }

    public void setAmountChangeInCash(Double amountChangeInCash) {
        this.amountChangeInCash = amountChangeInCash;
    }
}
