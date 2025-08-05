package com.revature.stocks.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * StockAnalytics Model Class
 * Represents calculated analytics for stock data
 */
public class StockAnalytics {
    
    private long id;
    private String symbol;
    private Date analysisDate;
    private BigDecimal dailyVolatility;
    private BigDecimal dailyPriceChange;
    private BigDecimal priceGap;
    private BigDecimal movingAvg7;
    private BigDecimal movingAvg30;
    private BigDecimal movingAvg90;
    private BigDecimal volumeTrend;
    private BigDecimal turnoverRatio;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    
    // Default constructor
    public StockAnalytics() {
    }
    
    // Constructor with essential fields
    public StockAnalytics(String symbol, Date analysisDate) {
        this.symbol = symbol;
        this.analysisDate = analysisDate;
    }
    
    // Constructor with calculated metrics
    public StockAnalytics(String symbol, Date analysisDate, BigDecimal dailyVolatility,
                          BigDecimal dailyPriceChange, BigDecimal priceGap,
                          BigDecimal movingAvg7, BigDecimal movingAvg30, BigDecimal movingAvg90) {
        this.symbol = symbol;
        this.analysisDate = analysisDate;
        this.dailyVolatility = dailyVolatility;
        this.dailyPriceChange = dailyPriceChange;
        this.priceGap = priceGap;
        this.movingAvg7 = movingAvg7;
        this.movingAvg30 = movingAvg30;
        this.movingAvg90 = movingAvg90;
    }
    
    // Full constructor
    public StockAnalytics(long id, String symbol, Date analysisDate, BigDecimal dailyVolatility,
                          BigDecimal dailyPriceChange, BigDecimal priceGap, BigDecimal movingAvg7,
                          BigDecimal movingAvg30, BigDecimal movingAvg90, BigDecimal volumeTrend,
                          BigDecimal turnoverRatio, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.symbol = symbol;
        this.analysisDate = analysisDate;
        this.dailyVolatility = dailyVolatility;
        this.dailyPriceChange = dailyPriceChange;
        this.priceGap = priceGap;
        this.movingAvg7 = movingAvg7;
        this.movingAvg30 = movingAvg30;
        this.movingAvg90 = movingAvg90;
        this.volumeTrend = volumeTrend;
        this.turnoverRatio = turnoverRatio;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Date getAnalysisDate() {
        return analysisDate;
    }
    
    public void setAnalysisDate(Date analysisDate) {
        this.analysisDate = analysisDate;
    }
    
    public BigDecimal getDailyVolatility() {
        return dailyVolatility;
    }
    
    public void setDailyVolatility(BigDecimal dailyVolatility) {
        this.dailyVolatility = dailyVolatility;
    }
    
    public BigDecimal getDailyPriceChange() {
        return dailyPriceChange;
    }
    
    public void setDailyPriceChange(BigDecimal dailyPriceChange) {
        this.dailyPriceChange = dailyPriceChange;
    }
    
    public BigDecimal getPriceGap() {
        return priceGap;
    }
    
    public void setPriceGap(BigDecimal priceGap) {
        this.priceGap = priceGap;
    }
    
    public BigDecimal getMovingAvg7() {
        return movingAvg7;
    }
    
    public void setMovingAvg7(BigDecimal movingAvg7) {
        this.movingAvg7 = movingAvg7;
    }
    
    public BigDecimal getMovingAvg30() {
        return movingAvg30;
    }
    
    public void setMovingAvg30(BigDecimal movingAvg30) {
        this.movingAvg30 = movingAvg30;
    }
    
    public BigDecimal getMovingAvg90() {
        return movingAvg90;
    }
    
    public void setMovingAvg90(BigDecimal movingAvg90) {
        this.movingAvg90 = movingAvg90;
    }
    
    public BigDecimal getVolumeTrend() {
        return volumeTrend;
    }
    
    public void setVolumeTrend(BigDecimal volumeTrend) {
        this.volumeTrend = volumeTrend;
    }
    
    public BigDecimal getTurnoverRatio() {
        return turnoverRatio;
    }
    
    public void setTurnoverRatio(BigDecimal turnoverRatio) {
        this.turnoverRatio = turnoverRatio;
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
    
    // Utility methods for analysis
    public boolean isHighVolatility() {
        return dailyVolatility != null && dailyVolatility.compareTo(new BigDecimal("5.0")) > 0;
    }
    
    public boolean isPositivePerformance() {
        return dailyPriceChange != null && dailyPriceChange.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public String getVolatilityCategory() {
        if (dailyVolatility == null) return "Unknown";
        
        if (dailyVolatility.compareTo(new BigDecimal("2.0")) <= 0) {
            return "Low";
        } else if (dailyVolatility.compareTo(new BigDecimal("5.0")) <= 0) {
            return "Medium";
        } else {
            return "High";
        }
    }
    
    public String getPerformanceCategory() {
        if (dailyPriceChange == null) return "Unknown";
        
        if (dailyPriceChange.compareTo(new BigDecimal("-2.0")) < 0) {
            return "Poor";
        } else if (dailyPriceChange.compareTo(new BigDecimal("2.0")) <= 0) {
            return "Stable";
        } else {
            return "Good";
        }
    }
    
    @Override
    public String toString() {
        return "StockAnalytics{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", analysisDate=" + analysisDate +
                ", dailyVolatility=" + dailyVolatility +
                ", dailyPriceChange=" + dailyPriceChange +
                ", priceGap=" + priceGap +
                ", movingAvg7=" + movingAvg7 +
                ", movingAvg30=" + movingAvg30 +
                ", movingAvg90=" + movingAvg90 +
                ", volumeTrend=" + volumeTrend +
                ", turnoverRatio=" + turnoverRatio +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        StockAnalytics that = (StockAnalytics) o;
        
        if (!symbol.equals(that.symbol)) return false;
        return analysisDate.equals(that.analysisDate);
    }
    
    @Override
    public int hashCode() {
        int result = symbol.hashCode();
        result = 31 * result + analysisDate.hashCode();
        return result;
    }
}