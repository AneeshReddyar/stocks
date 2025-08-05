package com.revature.stocks.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.revature.stocks.config.DatabaseConfig;
import com.revature.stocks.dao.DailyPriceDAO;
import com.revature.stocks.dao.StockDAO;
import com.revature.stocks.model.DailyPrice;
import com.revature.stocks.model.Stock;

/**
 * CSVImportService Class
 * Handles importing stock data from CSV files
 */
public class CSVImportService {
    
    private static final Logger logger = Logger.getLogger(CSVImportService.class.getName());
    private DailyPriceDAO dailyPriceDAO;
    private StockDAO stockDAO;
    private DatabaseConfig dbConfig;
    
    // Date formats for parsing CSV dates
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat ALT_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    
    public CSVImportService() {
        this.dailyPriceDAO = new DailyPriceDAO();
        this.stockDAO = new StockDAO();
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Import data from CSV file
     */
    public boolean importDataFromCSV(String csvFilePath) {
        logger.info("Starting CSV import from: " + csvFilePath);
        
        int totalRecords = 0;
        int successfulRecords = 0;
        int failedRecords = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    logger.info("CSV Header: " + line);
                    continue;
                }
                
                totalRecords++;
                
                try {
                    if (processCSVLine(line)) {
                        successfulRecords++;
                    } else {
                        failedRecords++;
                    }
                    
                    // Log progress every 1000 records
                    if (totalRecords % 1000 == 0) {
                        logger.info("Processed " + totalRecords + " records. Success: " + 
                                   successfulRecords + ", Failed: " + failedRecords);
                    }
                    
                } catch (Exception e) {
                    failedRecords++;
                    logger.warning("Error processing line " + totalRecords + ": " + e.getMessage());
                }
            }
            
            logger.info("CSV import completed. Total: " + totalRecords + 
                       ", Success: " + successfulRecords + ", Failed: " + failedRecords);
            
            return failedRecords == 0;
            
        } catch (IOException e) {
            logger.severe("Error reading CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process a single CSV line
     */
    private boolean processCSVLine(String line) {
        try {
            // Split CSV line by comma, handling quoted values
            String[] fields = parseCSVLine(line);
            
            if (fields.length < 14) {
                logger.warning("Insufficient fields in CSV line: " + line);
                return false;
            }
            
            // Parse fields based on expected CSV format
            // Expected format: SYMBOL,SERIES,DATE,PREV_CLOSE,OPEN,HIGH,LOW,LAST,CLOSE,VWAP,VOLUME,TURNOVER,TRADES,DELIVQTY,DELIV_PER
            String symbol = fields[0].trim();
            String series = fields[1].trim();
            String dateStr = fields[2].trim();
            
            // Parse date
            Date tradeDate = parseDate(dateStr);
            if (tradeDate == null) {
                logger.warning("Invalid date format: " + dateStr);
                return false;
            }
            
            // Parse numeric fields
            BigDecimal prevClose = parseBigDecimal(fields[3]);
            BigDecimal openPrice = parseBigDecimal(fields[4]);
            BigDecimal highPrice = parseBigDecimal(fields[5]);
            BigDecimal lowPrice = parseBigDecimal(fields[6]);
            BigDecimal lastPrice = parseBigDecimal(fields[7]);
            BigDecimal closePrice = parseBigDecimal(fields[8]);
            BigDecimal vwap = parseBigDecimal(fields[9]);
            
            Long volume = parseLong(fields[10]);
            BigDecimal turnover = parseBigDecimal(fields[11]);
            Integer trades = parseInteger(fields[12]);
            Long deliverableVolume = parseLong(fields[13]);
            BigDecimal deliverablePercentage = fields.length > 14 ? parseBigDecimal(fields[14]) : null;
            
            // Validate essential fields
            if (symbol.isEmpty() || openPrice == null || highPrice == null || 
                lowPrice == null || closePrice == null) {
                logger.warning("Missing essential fields in line: " + line);
                return false;
            }
            
            // Create DailyPrice object
            DailyPrice dailyPrice = new DailyPrice(
                symbol, tradeDate, series, prevClose, openPrice, highPrice,
                lowPrice, lastPrice, closePrice, vwap, volume, turnover,
                trades, deliverableVolume, deliverablePercentage
            );
            
            // Ensure stock exists
            ensureStockExists(symbol);
            
            // Insert daily price data
            return dailyPriceDAO.insertOrUpdateDailyPrice(dailyPrice);
            
        } catch (Exception e) {
            logger.severe("Error processing CSV line: " + line + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Parse CSV line handling quoted values
     */
    private String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
    
    /**
     * Parse date from string
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        dateStr = dateStr.replace("\"", "").trim();
        
        try {
            // Try primary date format
            java.util.Date utilDate = DATE_FORMAT.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e1) {
            try {
                // Try alternative date format
                java.util.Date utilDate = ALT_DATE_FORMAT.parse(dateStr);
                return new Date(utilDate.getTime());
            } catch (ParseException e2) {
                logger.warning("Could not parse date: " + dateStr);
                return null;
            }
        }
    }
    
    /**
     * Parse BigDecimal from string
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) {
            return null;
        }
        
        try {
            value = value.replace("\"", "").replace(",", "").trim();
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            logger.warning("Could not parse BigDecimal: " + value);
            return null;
        }
    }
    
    /**
     * Parse Long from string
     */
    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) {
            return null;
        }
        
        try {
            value = value.replace("\"", "").replace(",", "").trim();
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Could not parse Long: " + value);
            return null;
        }
    }
    
    /**
     * Parse Integer from string
     */
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) {
            return null;
        }
        
        try {
            value = value.replace("\"", "").replace(",", "").trim();
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Could not parse Integer: " + value);
            return null;
        }
    }
    
    /**
     * Ensure stock exists in database
     */
    private void ensureStockExists(String symbol) {
        try {
            Stock existingStock = stockDAO.findBySymbol(symbol);
            if (existingStock == null) {
                // Create a basic stock entry
                Stock newStock = new Stock(symbol, "Unknown Company", "Unknown Sector", BigDecimal.ZERO);
                stockDAO.insertOrUpdateStock(newStock);
                logger.info("Created new stock entry for: " + symbol);
            }
        } catch (Exception e) {
            logger.warning("Error ensuring stock exists for " + symbol + ": " + e.getMessage());
        }
    }
    
    /**
     * Import specific stock data from CSV
     */
    public boolean importStockDataFromCSV(String csvFilePath, String targetSymbol) {
        logger.info("Starting targeted CSV import for symbol: " + targetSymbol + " from: " + csvFilePath);
        
        int totalRecords = 0;
        int successfulRecords = 0;
        int failedRecords = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Quick check if line contains target symbol
                if (!line.contains(targetSymbol)) {
                    continue;
                }
                
                totalRecords++;
                
                try {
                    if (processCSVLine(line)) {
                        successfulRecords++;
                    } else {
                        failedRecords++;
                    }
                } catch (Exception e) {
                    failedRecords++;
                    logger.warning("Error processing line for " + targetSymbol + ": " + e.getMessage());
                }
            }
            
            logger.info("Targeted CSV import completed for " + targetSymbol + 
                       ". Total: " + totalRecords + ", Success: " + successfulRecords + 
                       ", Failed: " + failedRecords);
            
            return failedRecords == 0;
            
        } catch (IOException e) {
            logger.severe("Error reading CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate CSV file format
     */
    public boolean validateCSVFormat(String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String headerLine = reader.readLine();
            
            if (headerLine == null) {
                logger.severe("CSV file is empty: " + csvFilePath);
                return false;
            }
            
            // Check if header contains expected columns
            String[] headers = headerLine.split(",");
            logger.info("CSV has " + headers.length + " columns");
            
            // Basic validation - should have at least 14 columns
            if (headers.length < 14) {
                logger.severe("CSV file has insufficient columns. Expected at least 14, found: " + headers.length);
                return false;
            }
            
            // Check a few sample lines
            int sampleLines = 0;
            String line;
            while ((line = reader.readLine()) != null && sampleLines < 5) {
                String[] fields = parseCSVLine(line);
                if (fields.length < 14) {
                    logger.warning("Sample line has insufficient fields: " + line);
                }
                sampleLines++;
            }
            
            logger.info("CSV format validation completed successfully");
            return true;
            
        } catch (IOException e) {
            logger.severe("Error validating CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get import statistics
     */
    public String getImportStatistics(String csvFilePath) {
        try {
            StringBuilder stats = new StringBuilder();
            stats.append("=== CSV IMPORT STATISTICS ===\n");
            stats.append("File: ").append(csvFilePath).append("\n");
            
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                int totalLines = 0;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        stats.append("Header: ").append(line).append("\n");
                        continue;
                    }
                    totalLines++;
                }
                
                stats.append("Total Data Lines: ").append(totalLines).append("\n");
            }
            
            return stats.toString();
            
        } catch (IOException e) {
            logger.severe("Error getting import statistics: " + e.getMessage());
            return "Error getting statistics for: " + csvFilePath;
        }
    }
}