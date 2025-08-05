package com.revature.stocks.dao;

import com.revature.stocks.config.DatabaseConfig;
import com.revature.stocks.model.StockAnalytics;
import com.revature.stocks.util.QueryConstants;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * StockAnalytics Data Access Object
 * Handles all database operations for StockAnalytics entity
 */
public class StockAnalyticsDAO {
    
    private static final Logger logger = Logger.getLogger(StockAnalyticsDAO.class.getName());
    private DatabaseConfig dbConfig;
    
    public StockAnalyticsDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Insert or update stock analytics
     */
    public boolean insertOrUpdateAnalytics(StockAnalytics analytics) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.INSERT_STOCK_ANALYTICS);
            
            statement.setString(1, analytics.getSymbol());
            statement.setDate(2, analytics.getAnalysisDate());
            statement.setBigDecimal(3, analytics.getDailyVolatility());
            statement.setBigDecimal(4, analytics.getDailyPriceChange());
            statement.setBigDecimal(5, analytics.getPriceGap());
            statement.setBigDecimal(6, analytics.getMovingAvg7());
            statement.setBigDecimal(7, analytics.getMovingAvg30());
            statement.setBigDecimal(8, analytics.getMovingAvg90());
            statement.setBigDecimal(9, analytics.getVolumeTrend());
            statement.setBigDecimal(10, analytics.getTurnoverRatio());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Analytics inserted/updated: " + analytics.getSymbol() + 
                       " for " + analytics.getAnalysisDate() + ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("Error inserting/updating analytics for " + analytics.getSymbol() + 
                         " on " + analytics.getAnalysisDate() + ": " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Find analytics by symbol
     */
    public List<StockAnalytics> findBySymbol(String symbol) {
        List<StockAnalytics> analyticsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_ANALYTICS_BY_SYMBOL);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                analyticsList.add(mapResultSetToAnalytics(resultSet));
            }
            
            logger.info("Retrieved " + analyticsList.size() + " analytics records for " + symbol);
            
        } catch (SQLException e) {
            logger.severe("Error retrieving analytics for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return analyticsList;
    }
    
    /**
     * Find analytics by symbol and date range
     */
    public List<StockAnalytics> findBySymbolAndDateRange(String symbol, Date startDate, Date endDate) {
        List<StockAnalytics> analyticsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_ANALYTICS_BY_DATE_RANGE);
            statement.setString(1, symbol);
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                analyticsList.add(mapResultSetToAnalytics(resultSet));
            }
            
            logger.info("Retrieved " + analyticsList.size() + " analytics records for " + symbol + 
                       " between " + startDate + " and " + endDate);
            
        } catch (SQLException e) {
            logger.severe("Error retrieving analytics for " + symbol + 
                         " in date range: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return analyticsList;
    }
    
    /**
     * Calculate daily volatility for a symbol
     */
    public List<Map<String, Object>> calculateDailyVolatility(String symbol) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.CALCULATE_DAILY_VOLATILITY);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("trade_date", resultSet.getDate("trade_date"));
                result.put("daily_volatility", resultSet.getBigDecimal("daily_volatility"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error calculating daily volatility for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Calculate daily price change for a symbol
     */
    public List<Map<String, Object>> calculateDailyPriceChange(String symbol) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.CALCULATE_DAILY_PRICE_CHANGE);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("trade_date", resultSet.getDate("trade_date"));
                result.put("price_change", resultSet.getBigDecimal("price_change"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error calculating daily price change for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Calculate moving averages for a symbol
     */
    public List<Map<String, Object>> calculateMovingAverages(String symbol) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.CALCULATE_MOVING_AVERAGES);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("trade_date", resultSet.getDate("trade_date"));
                result.put("close_price", resultSet.getBigDecimal("close_price"));
                result.put("moving_avg_7", resultSet.getBigDecimal("moving_avg_7"));
                result.put("moving_avg_30", resultSet.getBigDecimal("moving_avg_30"));
                result.put("moving_avg_90", resultSet.getBigDecimal("moving_avg_90"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error calculating moving averages for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Calculate price gap for a symbol
     */
    public List<Map<String, Object>> calculatePriceGap(String symbol) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.CALCULATE_PRICE_GAP);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("trade_date", resultSet.getDate("trade_date"));
                result.put("price_gap", resultSet.getBigDecimal("price_gap"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error calculating price gap for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Compare stock performance
     */
    public List<Map<String, Object>> compareStockPerformance(Date fromDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.COMPARE_STOCK_PERFORMANCE);
            statement.setDate(1, fromDate);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("avg_volatility", resultSet.getBigDecimal("avg_volatility"));
                result.put("avg_price_change", resultSet.getBigDecimal("avg_price_change"));
                result.put("avg_volume_trend", resultSet.getBigDecimal("avg_volume_trend"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error comparing stock performance: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Get top performers
     */
    public List<Map<String, Object>> getTopPerformers(Date fromDate, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.GET_TOP_PERFORMERS);
            statement.setDate(1, fromDate);
            statement.setInt(2, limit);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("avg_performance", resultSet.getBigDecimal("avg_performance"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting top performers: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Rank stocks by volatility
     */
    public List<Map<String, Object>> rankStocksByVolatility(Date fromDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.RANK_STOCKS_BY_VOLATILITY);
            statement.setDate(1, fromDate);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("symbol", resultSet.getString("symbol"));
                result.put("avg_volatility", resultSet.getBigDecimal("avg_volatility"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.severe("Error ranking stocks by volatility: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Delete old analytics records
     */
    public int deleteOldAnalytics(Date beforeDate) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.DELETE_OLD_ANALYTICS);
            statement.setDate(1, beforeDate);
            
            int rowsDeleted = statement.executeUpdate();
            logger.info("Deleted " + rowsDeleted + " old analytics records before " + beforeDate);
            
            return rowsDeleted;
            
        } catch (SQLException e) {
            logger.severe("Error deleting old analytics: " + e.getMessage());
            return 0;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Map ResultSet to StockAnalytics object
     */
    private StockAnalytics mapResultSetToAnalytics(ResultSet resultSet) throws SQLException {
        StockAnalytics analytics = new StockAnalytics();
        analytics.setId(resultSet.getLong("id"));
        analytics.setSymbol(resultSet.getString("symbol"));
        analytics.setAnalysisDate(resultSet.getDate("analysis_date"));
        analytics.setDailyVolatility(resultSet.getBigDecimal("daily_volatility"));
        analytics.setDailyPriceChange(resultSet.getBigDecimal("daily_price_change"));
        analytics.setPriceGap(resultSet.getBigDecimal("price_gap"));
        analytics.setMovingAvg7(resultSet.getBigDecimal("moving_avg_7"));
        analytics.setMovingAvg30(resultSet.getBigDecimal("moving_avg_30"));
        analytics.setMovingAvg90(resultSet.getBigDecimal("moving_avg_90"));
        analytics.setVolumeTrend(resultSet.getBigDecimal("volume_trend"));
        analytics.setTurnoverRatio(resultSet.getBigDecimal("turnover_ratio"));
        analytics.setCreatedDate(resultSet.getTimestamp("created_date"));
        analytics.setUpdatedDate(resultSet.getTimestamp("updated_date"));
        return analytics;
    }
    
    /**
     * Close database resources
     */
    private void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            logger.warning("Error closing database resources: " + e.getMessage());
        }
    }
}