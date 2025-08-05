package com.revature.stocks.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.logging.Logger;

import com.revature.stocks.dao.DailyPriceDAO;
import com.revature.stocks.dao.StockDAO;
import com.revature.stocks.model.DailyPrice;
import com.revature.stocks.model.Stock;

/**
 * StockService Class
 * Business logic layer for stock operations
 */
public class StockService {
    
    private static final Logger logger = Logger.getLogger(StockService.class.getName());
    private StockDAO stockDAO;
    private DailyPriceDAO dailyPriceDAO;
    
    public StockService() {
        this.stockDAO = new StockDAO();
        this.dailyPriceDAO = new DailyPriceDAO();
    }
    
    /**
     * Add or update a stock
     */
    public boolean addOrUpdateStock(String symbol, String companyName, String sector, BigDecimal marketCap) {
        try {
            Stock stock = new Stock(symbol, companyName, sector, marketCap);
            boolean result = stockDAO.insertOrUpdateStock(stock);
            
            if (result) {
                logger.info("Successfully added/updated stock: " + symbol);
            } else {
                logger.warning("Failed to add/update stock: " + symbol);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.severe("Error in addOrUpdateStock for " + symbol + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get stock by symbol
     */
    public Stock getStockBySymbol(String symbol) {
        try {
            Stock stock = stockDAO.findBySymbol(symbol);
            if (stock != null) {
                logger.info("Retrieved stock: " + symbol);
            } else {
                logger.warning("Stock not found: " + symbol);
            }
            return stock;
            
        } catch (Exception e) {
            logger.severe("Error retrieving stock " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get all stocks
     */
    public List<Stock> getAllStocks() {
        try {
            List<Stock> stocks = stockDAO.findAllStocks();
            logger.info("Retrieved " + stocks.size() + " stocks");
            return stocks;
            
        } catch (Exception e) {
            logger.severe("Error retrieving all stocks: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update stock information
     */
    public boolean updateStockInfo(String symbol, String companyName, String sector, BigDecimal marketCap) {
        try {
            Stock existingStock = stockDAO.findBySymbol(symbol);
            if (existingStock == null) {
                logger.warning("Cannot update - stock not found: " + symbol);
                return false;
            }
            
            existingStock.setCompanyName(companyName);
            existingStock.setSector(sector);
            existingStock.setMarketCap(marketCap);
            
            boolean result = stockDAO.updateStock(existingStock);
            
            if (result) {
                logger.info("Successfully updated stock: " + symbol);
            } else {
                logger.warning("Failed to update stock: " + symbol);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.severe("Error updating stock " + symbol + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete stock
     */
    public boolean deleteStock(String symbol) {
        try {
            boolean result = stockDAO.deleteStock(symbol);
            
            if (result) {
                logger.info("Successfully deleted stock: " + symbol);
            } else {
                logger.warning("Failed to delete stock or stock not found: " + symbol);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.severe("Error deleting stock " + symbol + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if stock exists
     */
    public boolean doesStockExist(String symbol) {
        try {
            return stockDAO.stockExists(symbol);
        } catch (Exception e) {
            logger.severe("Error checking if stock exists " + symbol + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get available stock symbols
     */
    public List<String> getAvailableSymbols() {
        try {
            List<String> symbols = stockDAO.getAvailableSymbols();
            logger.info("Retrieved " + symbols.size() + " available symbols");
            return symbols;
            
        } catch (Exception e) {
            logger.severe("Error retrieving available symbols: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get latest price for a stock
     */
    public DailyPrice getLatestPrice(String symbol) {
        try {
            DailyPrice latestPrice = dailyPriceDAO.getLatestPrice(symbol);
            if (latestPrice != null) {
                logger.info("Retrieved latest price for: " + symbol + " - " + latestPrice.getClosePrice());
            } else {
                logger.warning("No price data found for: " + symbol);
            }
            return latestPrice;
            
        } catch (Exception e) {
            logger.severe("Error retrieving latest price for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get price history for a stock
     */
    public List<DailyPrice> getPriceHistory(String symbol) {
        try {
            List<DailyPrice> priceHistory = dailyPriceDAO.findBySymbol(symbol);
            logger.info("Retrieved " + priceHistory.size() + " price records for: " + symbol);
            return priceHistory;
            
        } catch (Exception e) {
            logger.severe("Error retrieving price history for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get price history within date range
     */
    public List<DailyPrice> getPriceHistoryInRange(String symbol, Date startDate, Date endDate) {
        try {
            List<DailyPrice> priceHistory = dailyPriceDAO.findBySymbolAndDateRange(symbol, startDate, endDate);
            logger.info("Retrieved " + priceHistory.size() + " price records for: " + symbol + 
                       " between " + startDate + " and " + endDate);
            return priceHistory;
            
        } catch (Exception e) {
            logger.severe("Error retrieving price history in range for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get date range for stock data
     */
    public Date[] getDataDateRange(String symbol) {
        try {
            Date[] dateRange = dailyPriceDAO.getDateRangeForSymbol(symbol);
            if (dateRange != null) {
                logger.info("Date range for " + symbol + ": " + dateRange[0] + " to " + dateRange[1]);
            } else {
                logger.warning("No date range found for: " + symbol);
            }
            return dateRange;
            
        } catch (Exception e) {
            logger.severe("Error retrieving date range for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate stock symbol format
     */
    public boolean isValidSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return false;
        }
        
        // Basic validation - alphanumeric and some special characters
        return symbol.matches("^[A-Z0-9\\-\\.]+$") && symbol.length() <= 50;
    }
    
    /**
     * Get stock performance summary
     */
    public String getStockSummary(String symbol) {
        try {
            Stock stock = getStockBySymbol(symbol);
            if (stock == null) {
                return "Stock not found: " + symbol;
            }
            
            DailyPrice latestPrice = getLatestPrice(symbol);
            Date[] dateRange = getDataDateRange(symbol);
            
            StringBuilder summary = new StringBuilder();
            summary.append("=== STOCK SUMMARY ===\n");
            summary.append("Symbol: ").append(stock.getSymbol()).append("\n");
            summary.append("Company: ").append(stock.getCompanyName()).append("\n");
            summary.append("Sector: ").append(stock.getSector()).append("\n");
            summary.append("Market Cap: ").append(stock.getMarketCap()).append("\n");
            
            if (latestPrice != null) {
                summary.append("Latest Price: ").append(latestPrice.getClosePrice()).append("\n");
                summary.append("Latest Date: ").append(latestPrice.getTradeDate()).append("\n");
                summary.append("Volume: ").append(latestPrice.getVolume()).append("\n");
            }
            
            if (dateRange != null) {
                summary.append("Data Range: ").append(dateRange[0]).append(" to ").append(dateRange[1]).append("\n");
            }
            
            return summary.toString();
            
        } catch (Exception e) {
            logger.severe("Error generating stock summary for " + symbol + ": " + e.getMessage());
            return "Error generating summary for: " + symbol;
        }
    }
}