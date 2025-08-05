package com.revature.stocks.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Stock Model Class
 * Represents a stock entity in the database
 */
public class Stock {
    
    private int id;
    private String symbol;
    private String companyName;
    private String sector;
    private BigDecimal marketCap;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    
    // Default constructor
    public Stock() {
    }
    
    // Constructor with essential fields
    public Stock(String symbol, String companyName, String sector, BigDecimal marketCap) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.marketCap = marketCap;
    }
    
    // Constructor with all fields
    public Stock(int id, String symbol, String companyName, String sector, 
                 BigDecimal marketCap, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.marketCap = marketCap;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getSector() {
        return sector;
    }
    
    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public BigDecimal getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    public Timestamp getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", sector='" + sector + '\'' +
                ", marketCap=" + marketCap +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Stock stock = (Stock) o;
        return symbol != null ? symbol.equals(stock.symbol) : stock.symbol == null;
    }
    
    @Override
    public int hashCode() {
        return symbol != null ? symbol.hashCode() : 0;
    }
}