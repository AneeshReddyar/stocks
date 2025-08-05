package com.revature.stocks.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.revature.stocks.config.DatabaseConfig;
import com.revature.stocks.model.DailyPrice;
import com.revature.stocks.util.QueryConstants;

/**
 * DailyPrice Data Access Object
 * Handles all database operations for DailyPrice entity
 */
public class DailyPriceDAO {
    
    private static final Logger logger = Logger.getLogger(DailyPriceDAO.class.getName());
    private DatabaseConfig dbConfig;
    
    public DailyPriceDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Insert or update daily price data
     */
    public boolean insertOrUpdateDailyPrice(DailyPrice dailyPrice) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.INSERT_DAILY_PRICE);
            
            statement.setString(1, dailyPrice.getSymbol());
            statement.setDate(2, dailyPrice.getTradeDate());
            statement.setString(3, dailyPrice.getSeries());
            statement.setBigDecimal(4, dailyPrice.getPrevClose());
            statement.setBigDecimal(5, dailyPrice.getOpenPrice());
            statement.setBigDecimal(6, dailyPrice.getHighPrice());
            statement.setBigDecimal(7, dailyPrice.getLowPrice());
            statement.setBigDecimal(8, dailyPrice.getLastPrice());
            statement.setBigDecimal(9, dailyPrice.getClosePrice());
            statement.setBigDecimal(10, dailyPrice.getVwap());
            
            if (dailyPrice.getVolume() != null) {
                statement.setLong(11, dailyPrice.getVolume());
            } else {
                statement.setNull(11, Types.BIGINT);
            }
            
            statement.setBigDecimal(12, dailyPrice.getTurnover());
            
            if (dailyPrice.getTrades() != null) {
                statement.setInt(13, dailyPrice.getTrades());
            } else {
                statement.setNull(13, Types.INTEGER);
            }
            
            if (dailyPrice.getDeliverableVolume() != null) {
                statement.setLong(14, dailyPrice.getDeliverableVolume());
            } else {
                statement.setNull(14, Types.BIGINT);
            }
            
            statement.setBigDecimal(15, dailyPrice.getDeliverablePercentage());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Daily price inserted/updated: " + dailyPrice.getSymbol() + 
                       " for " + dailyPrice.getTradeDate() + ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("Error inserting/updating daily price for " + dailyPrice.getSymbol() + 
                         " on " + dailyPrice.getTradeDate() + ": " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Find daily price by symbol and date
     */
    public DailyPrice findBySymbolAndDate(String symbol, Date date) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_DAILY_PRICE_BY_SYMBOL_DATE);
            statement.setString(1, symbol);
            statement.setDate(2, date);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToDailyPrice(resultSet);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding daily price for " + symbol + " on " + date + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Find all daily prices for a symbol
     */
    public List<DailyPrice> findBySymbol(String symbol) {
        List<DailyPrice> dailyPrices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_DAILY_PRICES_BY_SYMBOL);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                dailyPrices.add(mapResultSetToDailyPrice(resultSet));
            }
            
            logger.info("Retrieved " + dailyPrices.size() + " daily prices for " + symbol);
            
        } catch (SQLException e) {
            logger.severe("Error retrieving daily prices for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return dailyPrices;
    }
    
    /**
     * Find daily prices within date range
     */
    public List<DailyPrice> findBySymbolAndDateRange(String symbol, Date startDate, Date endDate) {
        List<DailyPrice> dailyPrices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_DAILY_PRICES_BY_DATE_RANGE);
            statement.setString(1, symbol);
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                dailyPrices.add(mapResultSetToDailyPrice(resultSet));
            }
            
            logger.info("Retrieved " + dailyPrices.size() + " daily prices for " + symbol + 
                       " between " + startDate + " and " + endDate);
            
        } catch (SQLException e) {
            logger.severe("Error retrieving daily prices for " + symbol + 
                         " in date range: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return dailyPrices;
    }
    
    /**
     * Get latest price for a symbol
     */
    public DailyPrice getLatestPrice(String symbol) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_LATEST_PRICE_BY_SYMBOL);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToDailyPrice(resultSet);
            }
            
        } catch (SQLException e) {
            logger.severe("Error retrieving latest price for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Get all daily prices
     */
    public List<DailyPrice> findAllDailyPrices() {
        List<DailyPrice> dailyPrices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_ALL_DAILY_PRICES);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                dailyPrices.add(mapResultSetToDailyPrice(resultSet));
            }
            
            logger.info("Retrieved " + dailyPrices.size() + " total daily prices");
            
        } catch (SQLException e) {
            logger.severe("Error retrieving all daily prices: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return dailyPrices;
    }
    
    /**
     * Get date range for a symbol
     */
    public Date[] getDateRangeForSymbol(String symbol) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.GET_DATE_RANGE_FOR_SYMBOL);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                return new Date[]{startDate, endDate};
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting date range for " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to DailyPrice object
     */
    private DailyPrice mapResultSetToDailyPrice(ResultSet resultSet) throws SQLException {
        DailyPrice dailyPrice = new DailyPrice();
        dailyPrice.setId(resultSet.getLong("id"));
        dailyPrice.setSymbol(resultSet.getString("symbol"));
        dailyPrice.setTradeDate(resultSet.getDate("trade_date"));
        dailyPrice.setSeries(resultSet.getString("series"));
        dailyPrice.setPrevClose(resultSet.getBigDecimal("prev_close"));
        dailyPrice.setOpenPrice(resultSet.getBigDecimal("open_price"));
        dailyPrice.setHighPrice(resultSet.getBigDecimal("high_price"));
        dailyPrice.setLowPrice(resultSet.getBigDecimal("low_price"));
        dailyPrice.setLastPrice(resultSet.getBigDecimal("last_price"));
        dailyPrice.setClosePrice(resultSet.getBigDecimal("close_price"));
        dailyPrice.setVwap(resultSet.getBigDecimal("vwap"));
        
        Long volume = resultSet.getLong("volume");
        if (!resultSet.wasNull()) {
            dailyPrice.setVolume(volume);
        }
        
        dailyPrice.setTurnover(resultSet.getBigDecimal("turnover"));
        
        Integer trades = resultSet.getInt("trades");
        if (!resultSet.wasNull()) {
            dailyPrice.setTrades(trades);
        }
        
        Long deliverableVolume = resultSet.getLong("deliverable_volume");
        if (!resultSet.wasNull()) {
            dailyPrice.setDeliverableVolume(deliverableVolume);
        }
        
        dailyPrice.setDeliverablePercentage(resultSet.getBigDecimal("deliverable_percentage"));
        dailyPrice.setCreatedDate(resultSet.getTimestamp("created_date"));
        
        return dailyPrice;
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