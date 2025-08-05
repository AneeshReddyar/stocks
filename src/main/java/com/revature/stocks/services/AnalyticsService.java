package com.revature.stocks.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.revature.stocks.dao.DailyPriceDAO;
import com.revature.stocks.dao.StockAnalyticsDAO;
import com.revature.stocks.model.DailyPrice;
import com.revature.stocks.model.StockAnalytics;

/**
 * AnalyticsService Class
 * Business logic layer for stock analytics operations
 */
public class AnalyticsService {
    
    private static final Logger logger = Logger.getLogger(AnalyticsService.class.getName());
    private StockAnalyticsDAO analyticsDAO;
    private DailyPriceDAO dailyPriceDAO;
    
    public AnalyticsService() {
        this.analyticsDAO = new StockAnalyticsDAO();
        this.dailyPriceDAO = new DailyPriceDAO();
    }
    
    /**
     * Calculate and store analytics for a stock
     */
    public boolean calculateAndStoreAnalytics(String symbol, Date analysisDate) {
        try {
            // Get price data for calculations
            List<DailyPrice> priceData = dailyPriceDAO.findBySymbol(symbol);
            if (priceData.isEmpty()) {
                logger.warning("No price data available for analytics calculation: " + symbol);
                return false;
            }
            
            // Find the specific date's data
            DailyPrice currentData = null;
            for (DailyPrice dp : priceData) {
                if (dp.getTradeDate().equals(analysisDate)) {
                    currentData = dp;
                    break;
                }
            }
            
            if (currentData == null) {
                logger.warning("No price data found for " + symbol + " on " + analysisDate);
                return false;
            }
            
            // Calculate analytics
            StockAnalytics analytics = new StockAnalytics(symbol, analysisDate);
            
            // Calculate daily volatility
            BigDecimal dailyVolatility = currentData.calculateDailyVolatility();
            analytics.setDailyVolatility(dailyVolatility);
            
            // Calculate daily price change
            BigDecimal dailyPriceChange = currentData.calculateDailyPriceChange();
            analytics.setDailyPriceChange(dailyPriceChange);
            
            // Calculate price gap (difference between open and previous close)
            if (currentData.getPrevClose() != null && currentData.getOpenPrice() != null) {
                BigDecimal priceGap = currentData.getOpenPrice().subtract(currentData.getPrevClose());
                analytics.setPriceGap(priceGap);
            }
            
            // Calculate moving averages (simplified calculation)
            analytics.setMovingAvg7(calculateSimpleMovingAverage(priceData, analysisDate, 7));
            analytics.setMovingAvg30(calculateSimpleMovingAverage(priceData, analysisDate, 30));
            analytics.setMovingAvg90(calculateSimpleMovingAverage(priceData, analysisDate, 90));
            
            // Calculate volume trend (simplified)
            BigDecimal volumeTrend = calculateVolumeTrend(priceData, analysisDate);
            analytics.setVolumeTrend(volumeTrend);
            
            // Calculate turnover ratio
            if (currentData.getTurnover() != null && currentData.getVolume() != null && 
                currentData.getVolume() > 0) {
                BigDecimal turnoverRatio = currentData.getTurnover()
                    .divide(new BigDecimal(currentData.getVolume()), 4, BigDecimal.ROUND_HALF_UP);
                analytics.setTurnoverRatio(turnoverRatio);
            }
            
            // Store analytics
            boolean result = analyticsDAO.insertOrUpdateAnalytics(analytics);
            
            if (result) {
                logger.info("Successfully calculated and stored analytics for " + symbol + " on " + analysisDate);
            } else {
                logger.warning("Failed to store analytics for " + symbol + " on " + analysisDate);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.severe("Error calculating analytics for " + symbol + " on " + analysisDate + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get analytics for a stock
     */
    public List<StockAnalytics> getAnalytics(String symbol) {
        try {
            List<StockAnalytics> analytics = analyticsDAO.findBySymbol(symbol);
            logger.info("Retrieved " + analytics.size() + " analytics records for: " + symbol);
            return analytics;
            
        } catch (Exception e) {
            logger.severe("Error retrieving analytics for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get analytics within date range
     */
    public List<StockAnalytics> getAnalyticsInRange(String symbol, Date startDate, Date endDate) {
        try {
            List<StockAnalytics> analytics = analyticsDAO.findBySymbolAndDateRange(symbol, startDate, endDate);
            logger.info("Retrieved " + analytics.size() + " analytics records for " + symbol + 
                       " between " + startDate + " and " + endDate);
            return analytics;
            
        } catch (Exception e) {
            logger.severe("Error retrieving analytics in range for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate daily volatility for all dates
     */
    public List<Map<String, Object>> calculateDailyVolatility(String symbol) {
        try {
            List<Map<String, Object>> volatilityData = analyticsDAO.calculateDailyVolatility(symbol);
            logger.info("Calculated daily volatility for " + volatilityData.size() + " dates for: " + symbol);
            return volatilityData;
            
        } catch (Exception e) {
            logger.severe("Error calculating daily volatility for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate daily price changes
     */
    public List<Map<String, Object>> calculateDailyPriceChanges(String symbol) {
        try {
            List<Map<String, Object>> priceChangeData = analyticsDAO.calculateDailyPriceChange(symbol);
            logger.info("Calculated daily price changes for " + priceChangeData.size() + " dates for: " + symbol);
            return priceChangeData;
            
        } catch (Exception e) {
            logger.severe("Error calculating daily price changes for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate moving averages
     */
    public List<Map<String, Object>> calculateMovingAverages(String symbol) {
        try {
            List<Map<String, Object>> movingAvgData = analyticsDAO.calculateMovingAverages(symbol);
            logger.info("Calculated moving averages for " + movingAvgData.size() + " dates for: " + symbol);
            return movingAvgData;
            
        } catch (Exception e) {
            logger.severe("Error calculating moving averages for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate price gaps
     */
    public List<Map<String, Object>> calculatePriceGaps(String symbol) {
        try {
            List<Map<String, Object>> priceGapData = analyticsDAO.calculatePriceGap(symbol);
            logger.info("Calculated price gaps for " + priceGapData.size() + " dates for: " + symbol);
            return priceGapData;
            
        } catch (Exception e) {
            logger.severe("Error calculating price gaps for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Compare stock performance
     */
    public List<Map<String, Object>> compareStockPerformance(Date fromDate) {
        try {
            List<Map<String, Object>> performanceData = analyticsDAO.compareStockPerformance(fromDate);
            logger.info("Compared performance for " + performanceData.size() + " stocks from: " + fromDate);
            return performanceData;
            
        } catch (Exception e) {
            logger.severe("Error comparing stock performance from " + fromDate + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get top performing stocks
     */
    public List<Map<String, Object>> getTopPerformers(Date fromDate, int limit) {
        try {
            List<Map<String, Object>> topPerformers = analyticsDAO.getTopPerformers(fromDate, limit);
            logger.info("Retrieved top " + limit + " performers from: " + fromDate);
            return topPerformers;
            
        } catch (Exception e) {
            logger.severe("Error getting top performers from " + fromDate + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Rank stocks by volatility
     */
    public List<Map<String, Object>> rankStocksByVolatility(Date fromDate) {
        try {
            List<Map<String, Object>> volatilityRanking = analyticsDAO.rankStocksByVolatility(fromDate);
            logger.info("Retrieved volatility ranking for " + volatilityRanking.size() + " stocks from: " + fromDate);
            return volatilityRanking;
            
        } catch (Exception e) {
            logger.severe("Error ranking stocks by volatility from " + fromDate + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate analytics summary report
     */
    public String generateAnalyticsSummary(String symbol, Date fromDate) {
        try {
            StringBuilder summary = new StringBuilder();
            summary.append("=== ANALYTICS SUMMARY FOR ").append(symbol).append(" ===\n");
            summary.append("Analysis Period: From ").append(fromDate).append("\n\n");
            
            // Get basic analytics
            List<StockAnalytics> analytics = getAnalyticsInRange(symbol, fromDate, new Date(System.currentTimeMillis()));
            
            if (analytics.isEmpty()) {
                summary.append("No analytics data available for the specified period.\n");
                return summary.toString();
            }
            
            // Calculate averages
            BigDecimal avgVolatility = BigDecimal.ZERO;
            BigDecimal avgPriceChange = BigDecimal.ZERO;
            int count = 0;
            
            for (StockAnalytics analytic : analytics) {
                if (analytic.getDailyVolatility() != null) {
                    avgVolatility = avgVolatility.add(analytic.getDailyVolatility());
                }
                if (analytic.getDailyPriceChange() != null) {
                    avgPriceChange = avgPriceChange.add(analytic.getDailyPriceChange());
                }
                count++;
            }
            
            if (count > 0) {
                avgVolatility = avgVolatility.divide(new BigDecimal(count), 4, BigDecimal.ROUND_HALF_UP);
                avgPriceChange = avgPriceChange.divide(new BigDecimal(count), 4, BigDecimal.ROUND_HALF_UP);
            }
            
            summary.append("Number of Analysis Days: ").append(count).append("\n");
            summary.append("Average Daily Volatility: ").append(avgVolatility).append("%\n");
            summary.append("Average Daily Price Change: ").append(avgPriceChange).append("%\n");
            
            // Latest analytics
            if (!analytics.isEmpty()) {
                StockAnalytics latest = analytics.get(0); // Assuming sorted by date desc
                summary.append("\nLatest Analytics (").append(latest.getAnalysisDate()).append("):\n");
                summary.append("- Daily Volatility: ").append(latest.getDailyVolatility()).append("%\n");
                summary.append("- Daily Price Change: ").append(latest.getDailyPriceChange()).append("%\n");
                summary.append("- 7-Day Moving Average: ").append(latest.getMovingAvg7()).append("\n");
                summary.append("- 30-Day Moving Average: ").append(latest.getMovingAvg30()).append("\n");
                summary.append("- Volatility Category: ").append(latest.getVolatilityCategory()).append("\n");
                summary.append("- Performance Category: ").append(latest.getPerformanceCategory()).append("\n");
            }
            
            return summary.toString();
            
        } catch (Exception e) {
            logger.severe("Error generating analytics summary for " + symbol + ": " + e.getMessage());
            return "Error generating analytics summary for: " + symbol;
        }
    }
    
    /**
     * Helper method to calculate simple moving average
     */
    private BigDecimal calculateSimpleMovingAverage(List<DailyPrice> priceData, Date targetDate, int days) {
        try {
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;
            
            for (DailyPrice price : priceData) {
                if (price.getTradeDate().compareTo(targetDate) <= 0 && count < days) {
                    sum = sum.add(price.getClosePrice());
                    count++;
                }
            }
            
            if (count > 0) {
                return sum.divide(new BigDecimal(count), 4, BigDecimal.ROUND_HALF_UP);
            }
            
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.warning("Error calculating moving average: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Helper method to calculate volume trend
     */
    private BigDecimal calculateVolumeTrend(List<DailyPrice> priceData, Date targetDate) {
        try {
            // Simple volume trend calculation - compare current volume with average of last 5 days
            Long currentVolume = null;
            BigDecimal avgVolume = BigDecimal.ZERO;
            int count = 0;
            
            for (DailyPrice price : priceData) {
                if (price.getTradeDate().equals(targetDate)) {
                    currentVolume = price.getVolume();
                } else if (price.getTradeDate().compareTo(targetDate) < 0 && count < 5) {
                    if (price.getVolume() != null) {
                        avgVolume = avgVolume.add(new BigDecimal(price.getVolume()));
                        count++;
                    }
                }
            }
            
            if (currentVolume != null && count > 0) {
                avgVolume = avgVolume.divide(new BigDecimal(count), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal currentVol = new BigDecimal(currentVolume);
                
                if (avgVolume.compareTo(BigDecimal.ZERO) > 0) {
                    return currentVol.subtract(avgVolume).divide(avgVolume, 4, BigDecimal.ROUND_HALF_UP)
                                    .multiply(new BigDecimal("100"));
                }
            }
            
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.warning("Error calculating volume trend: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Clean up old analytics data
     */
    public int cleanupOldAnalytics(Date beforeDate) {
        try {
            int deletedRecords = analyticsDAO.deleteOldAnalytics(beforeDate);
            logger.info("Cleaned up " + deletedRecords + " old analytics records before " + beforeDate);
            return deletedRecords;
            
        } catch (Exception e) {
            logger.severe("Error cleaning up old analytics: " + e.getMessage());
            return 0;
        }
    }
}