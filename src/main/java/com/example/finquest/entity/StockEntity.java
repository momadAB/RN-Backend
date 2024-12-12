package com.example.finquest.entity;
import javax.persistence.*;
import java.util.List;

@Entity
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName; // e.g., Apple Inc., Google LLC

    @Column(nullable = false)
    private Double stockPrice; // Current price of the stock

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OwnedStockEntity> ownedStocks;

    // Getters and Setters


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<OwnedStockEntity> getOwnedStocks() {
        return ownedStocks;
    }

    public void setOwnedStocks(List<OwnedStockEntity> ownedStocks) {
        this.ownedStocks = ownedStocks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }
}
