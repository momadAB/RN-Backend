package com.example.finquest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class OwnedStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    @JsonIgnore
    private ChildUserEntity childUser;

    @Column(nullable = false)
    private Double amount; // Amount of cash invested

    @Column
    private Double amountOfStocks;

    @Column
    private Double stopLoss;

    @Column
    private Double takeProfit;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private StockEntity stock;

    public OwnedStockEntity() {}

    public OwnedStockEntity(ChildUserEntity childUser, StockEntity stock, Double amount, Double amountOfStocks) {
        this.childUser = childUser;
        this.stock = stock;
        this.amount = amount;
        this.amountOfStocks = amountOfStocks;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmountOfStocks() {
        return amountOfStocks;
    }

    public void setAmountOfStocks(Double amountOfStocks) {
        this.amountOfStocks = amountOfStocks;
    }

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

    public ChildUserEntity getChildUser() {
        return childUser;
    }

    public void setChildUser(ChildUserEntity childUser) {
        this.childUser = childUser;
    }

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return  "Child username=" + (childUser != null ? childUser.getUsername() : "null") + ", " +
                "Stock amount=" + amount + ", " +
                "Company=" + (stock != null ? stock.getCompanyName() : "null");
    }
}
