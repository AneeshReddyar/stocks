package com.revature.stocks.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * DailyPrice Model Class
 * Represents daily stock price data from CSV
 */
public class DailyPrice {
    
    private long id;
    private String symbol;
    private Date tradeDate;
    private String series;
    private BigDecimal prevClose;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal lastPrice;
    private BigDecimal closePrice;
    private BigDecimal vwap;
    private Long volume;
    private BigDecimal turnover;
    private Integer trades;
    private Long deliverableVolume;
    private BigDecimal deliverablePercentage;
    private Timestamp createdDate;
    
    // Default constructor
    public DailyPrice() {
    }
    
    // Constructor with essential fields
    public DailyPrice(String symbol, Date tradeDate, BigDecimal openPrice, 
                      BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
    }
    
    // Constructor for CSV data parsing
    public DailyPrice(String symbol, Date tradeDate, String series, BigDecimal prevClose,
                      BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice,
                      BigDecimal lastPrice, BigDecimal closePrice, BigDecimal vwap,
                      Long volume, BigDecimal turnover, Integer trades,
                      Long deliverableVolume, BigDecimal deliverablePercentage) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.series = series;
        this.prevClose = prevClose;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.lastPrice = lastPrice;
        this.closePrice = closePrice;
        this.vwap = vwap;
        this.volume = volume;
        this.turnover = turnover;
        this.trades = trades;
        this.deliverableVolume = deliverableVolume;
        this.deliverablePercentage = deliverablePercentage;
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
    
    public Date getTradeDate() {
        return tradeDate;
    }
    
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }
    
    public String getSeries() {
        return series;
    }
    
    public void setSeries(String series) {
        this.series = series;
    }
    
    public BigDecimal getPrevClose() {
        return prevClose;
    }
    
    public void setPrevClose(BigDecimal prevClose) {
        this.prevClose = prevClose;
    }
    
    public BigDecimal getOpenPrice() {
        return openPrice;
    }
    
    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }
    
    public BigDecimal getHighPrice() {
        return highPrice;
    }
    
    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }
    
    public BigDecimal getLowPrice() {
        return lowPrice;
    }
    
    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }
    
    public BigDecimal getLastPrice() {
        return lastPrice;
    }
    
    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }
    
    public BigDecimal getClosePrice() {
        return closePrice;
    }
    
    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }
    
    public BigDecimal getVwap() {
        return vwap;
    }
    
    public void setVwap(BigDecimal vwap) {
        this.vwap = vwap;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    public BigDecimal getTurnover() {
        return turnover;
    }
    
    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
    
    public Integer getTrades() {
        return trades;
    }
    
    public void setTrades(Integer trades) {
        this.trades = trades;
    }
    
    public Long getDeliverableVolume() {
        return deliverableVolume;
    }
    
    public void setDeliverableVolume(Long deliverableVolume) {
        this.deliverableVolume = deliverableVolume;
    }
    
    public BigDecimal getDeliverablePercentage() {
        return deliverablePercentage;
    }
    
    public void setDeliverablePercentage(BigDecimal deliverablePercentage) {
        this.deliverablePercentage = deliverablePercentage;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    // Utility methods for calculations
    public BigDecimal calculateDailyVolatility() {
        if (openPrice != null && openPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = highPrice.subtract(lowPrice);
            return diff.divide(openPrice, 4, BigDecimal.ROUND_HALF_UP)
                      .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calculateDailyPriceChange() {
        if (openPrice != null && openPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = closePrice.subtract(openPrice);
            return diff.divide(openPrice, 4, BigDecimal.ROUND_HALF_UP)
                      .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calculateTurnover() {
        if (closePrice != null && volume != null) {
            return closePrice.multiply(new BigDecimal(volume));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "DailyPrice{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", tradeDate=" + tradeDate +
                ", series='" + series + '\'' +
                ", prevClose=" + prevClose +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", lastPrice=" + lastPrice +
                ", closePrice=" + closePrice +
                ", vwap=" + vwap +
                ", volume=" + volume +
                ", turnover=" + turnover +
                ", trades=" + trades +
                ", deliverableVolume=" + deliverableVolume +
                ", deliverablePercentage=" + deliverablePercentage +
                ", createdDate=" + createdDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DailyPrice that = (DailyPrice) o;
        
        if (!symbol.equals(that.symbol)) return false;
        return tradeDate.equals(that.tradeDate);
    }
    
    @Override
    public int hashCode() {
        int result = symbol.hashCode();
        result = 31 * result + tradeDate.hashCode();
        return result;
    }
}