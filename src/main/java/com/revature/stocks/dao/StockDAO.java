package com.revature.stocks.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.revature.stocks.config.DatabaseConfig;
import com.revature.stocks.model.Stock;
import com.revature.stocks.util.QueryConstants;

/**
 * Stock Data Access Object
 * Handles all database operations for Stock entity
 */
public class StockDAO {
    
    private static final Logger logger = Logger.getLogger(StockDAO.class.getName());
    private DatabaseConfig dbConfig;
    
    public StockDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Insert or update a stock
     */
    public boolean insertOrUpdateStock(Stock stock) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.INSERT_STOCK);
            
            statement.setString(1, stock.getSymbol());
            statement.setString(2, stock.getCompanyName());
            statement.setString(3, stock.getSector());
            statement.setBigDecimal(4, stock.getMarketCap());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Stock inserted/updated: " + stock.getSymbol() + ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("Error inserting/updating stock " + stock.getSymbol() + ": " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Find stock by symbol
     */
    public Stock findBySymbol(String symbol) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_STOCK_BY_SYMBOL);
            statement.setString(1, symbol);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToStock(resultSet);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding stock by symbol " + symbol + ": " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Get all stocks
     */
    public List<Stock> findAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.SELECT_ALL_STOCKS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                stocks.add(mapResultSetToStock(resultSet));
            }
            
            logger.info("Retrieved " + stocks.size() + " stocks from database");
            
        } catch (SQLException e) {
            logger.severe("Error retrieving all stocks: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return stocks;
    }
    
    /**
     * Update stock details
     */
    public boolean updateStock(Stock stock) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.UPDATE_STOCK);
            
            statement.setString(1, stock.getCompanyName());
            statement.setString(2, stock.getSector());
            statement.setBigDecimal(3, stock.getMarketCap());
            statement.setString(4, stock.getSymbol());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Stock updated: " + stock.getSymbol() + ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating stock " + stock.getSymbol() + ": " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Delete stock by symbol
     */
    public boolean deleteStock(String symbol) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.DELETE_STOCK);
            statement.setString(1, symbol);
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Stock deleted: " + symbol + ", Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting stock " + symbol + ": " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Check if stock exists
     */
    public boolean stockExists(String symbol) {
        return findBySymbol(symbol) != null;
    }
    
    /**
     * Get available symbols
     */
    public List<String> getAvailableSymbols() {
        List<String> symbols = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConfig.getConnection();
            statement = connection.prepareStatement(QueryConstants.GET_AVAILABLE_SYMBOLS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                symbols.add(resultSet.getString("symbol"));
            }
            
        } catch (SQLException e) {
            logger.severe("Error retrieving available symbols: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return symbols;
    }
    
    /**
     * Map ResultSet to Stock object
     */
    private Stock mapResultSetToStock(ResultSet resultSet) throws SQLException {
        Stock stock = new Stock();
        stock.setId(resultSet.getInt("id"));
        stock.setSymbol(resultSet.getString("symbol"));
        stock.setCompanyName(resultSet.getString("company_name"));
        stock.setSector(resultSet.getString("sector"));
        stock.setMarketCap(resultSet.getBigDecimal("market_cap"));
        stock.setCreatedDate(resultSet.getTimestamp("created_date"));
        stock.setUpdatedDate(resultSet.getTimestamp("updated_date"));
        return stock;
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