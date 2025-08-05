package com.revature.stocks.util;

import com.revature.stocks.model.DailyPrice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * CSV Reader Utility Class
 * Handles reading and parsing of CSV files
 */
public class CSVReader {
    
    private static final Logger logger = Logger.getLogger(CSVReader.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Read daily prices from CSV file
     */
    public static List<DailyPrice> readDailyPricesFromCSV(String csvFilePath) {
        List<DailyPrice> dailyPrices = new ArrayList<>();
        String line;
        String csvSplitBy = ",";
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            
            // Skip header line
            String headerLine = br.readLine();
            if (headerLine != null) {
                logger.info("CSV Header: " + headerLine);
                lineNumber++;
            }
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                try {
                    // Split the line by comma
                    String[] data = line.split(csvSplitBy);
                    
                    // Validate data length
                    if (data.length < 14) {
                        logger.warning("Insufficient data at line " + lineNumber + ": " + line);
                        errorCount++;
                        continue;
                    }
                    
                    // Parse and create DailyPrice object
                    DailyPrice dailyPrice = parseDailyPriceFromArray(data, lineNumber);
                    
                    if (dailyPrice != null) {
                        dailyPrices.add(dailyPrice);
                        successCount++;
                    } else {
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    logger.severe("Error parsing line " + lineNumber + ": " + line + " - " + e.getMessage());
                    errorCount++;
                }
            }
            
            logger.info("CSV parsing completed. Total lines: " + lineNumber + 
                       ", Success: " + successCount + ", Errors: " + errorCount);
            
        } catch (IOException e) {
            logger.severe("Error reading CSV file " + csvFilePath + ": " + e.getMessage());
        }
        
        return dailyPrices;
    }
    
    /**
     * Parse DailyPrice object from CSV data array
     */
    private static DailyPrice parseDailyPriceFromArray(String[] data, int lineNumber) {
        try {
            // CSV format: Date,Symbol,Series,Prev Close,Open,High,Low,Last,Close,VWAP,Volume,Turnover,Trades,Deliverable Volume,%Deliverble
            
            DailyPrice dailyPrice = new DailyPrice();
            
            // Parse date (index 0)
            String dateStr = data[0].trim();
            Date tradeDate = parseDate(dateStr);
            if (tradeDate == null) {
                logger.warning("Invalid date format at line " + lineNumber + ": " + dateStr);
                return null;
            }
            dailyPrice.setTradeDate(tradeDate);
            
            // Parse symbol (index 1)
            String symbol = data[1].trim();
            if (symbol.isEmpty()) {
                logger.warning("Empty symbol at line " + lineNumber);
                return null;
            }
            dailyPrice.setSymbol(symbol);
            
            // Parse series (index 2)
            dailyPrice.setSeries(data[2].trim());
            
            // Parse prev close (index 3)
            dailyPrice.setPrevClose(parseBigDecimal(data[3].trim()));
            
            // Parse open price (index 4) - required
            BigDecimal openPrice = parseBigDecimal(data[4].trim());
            if (openPrice == null) {
                logger.warning("Invalid open price at line " + lineNumber + ": " + data[4]);
                return null;
            }
            dailyPrice.setOpenPrice(openPrice);
            
            // Parse high price (index 5) - required
            BigDecimal highPrice = parseBigDecimal(data[5].trim());
            if (highPrice == null) {
                logger.warning("Invalid high price at line " + lineNumber + ": " + data[5]);
                return null;
            }
            dailyPrice.setHighPrice(highPrice);
            
            // Parse low price (index 6) - required
            BigDecimal lowPrice = parseBigDecimal(data[6].trim());
            if (lowPrice == null) {
                logger.warning("Invalid low price at line " + lineNumber + ": " + data[6]);
                return null;
            }
            dailyPrice.setLowPrice(lowPrice);
            
            // Parse last price (index 7)
            dailyPrice.setLastPrice(parseBigDecimal(data[7].trim()));
            
            // Parse close price (index 8) - required
            BigDecimal closePrice = parseBigDecimal(data[8].trim());
            if (closePrice == null) {
                logger.warning("Invalid close price at line " + lineNumber + ": " + data[8]);
                return null;
            }
            dailyPrice.setClosePrice(closePrice);
            
            // Parse VWAP (index 9)
            dailyPrice.setVwap(parseBigDecimal(data[9].trim()));
            
            // Parse volume (index 10)
            dailyPrice.setVolume(parseLong(data[10].trim()));
            
            // Parse turnover (index 11)
            dailyPrice.setTurnover(parseBigDecimal(data[11].trim()));
            
            // Parse trades (index 12) - optional
            if (data.length > 12 && !data[12].trim().isEmpty()) {
                dailyPrice.setTrades(parseInteger(data[12].trim()));
            }
            
            // Parse deliverable volume (index 13) - optional
            if (data.length > 13 && !data[13].trim().isEmpty()) {
                dailyPrice.setDeliverableVolume(parseLong(data[13].trim()));
            }
            
            // Parse deliverable percentage (index 14) - optional
            if (data.length > 14 && !data[14].trim().isEmpty()) {
                dailyPrice.setDeliverablePercentage(parseBigDecimal(data[14].trim()));
            }
            
            return dailyPrice;
            
        } catch (Exception e) {
            logger.severe("Error parsing daily price at line " + lineNumber + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse date string to SQL Date
     */
    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            java.util.Date utilDate = DATE_FORMAT.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            logger.warning("Error parsing date: " + dateStr + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse string to BigDecimal
     */
    private static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Remove any commas or currency symbols
            String cleanValue = value.replaceAll("[,₹$]", "");
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            logger.warning("Error parsing BigDecimal: " + value);
            return null;
        }
    }
    
    /**
     * Parse string to Long
     */
    private static Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Remove any commas
            String cleanValue = value.replaceAll(",", "");
            return Long.parseLong(cleanValue);
        } catch (NumberFormatException e) {
            logger.warning("Error parsing Long: " + value);
            return null;
        }
    }
    
    /**
     * Parse string to Integer
     */
    private static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Remove any commas
            String cleanValue = value.replaceAll(",", "");
            return Integer.parseInt(cleanValue);
        } catch (NumberFormatException e) {
            logger.warning("Error parsing Integer: " + value);
            return null;
        }
    }
    
    /**
     * Validate CSV file format
     */
    public static boolean validateCSVFormat(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String headerLine = br.readLine();
            
            if (headerLine == null) {
                logger.severe("CSV file is empty: " + csvFilePath);
                return false;
            }
            
            // Expected header format
            String expectedHeaders = "Date,Symbol,Series,Prev Close,Open,High,Low,Last,Close,VWAP,Volume,Turnover";
            
            if (!headerLine.toLowerCase().contains("date") || 
                !headerLine.toLowerCase().contains("symbol") ||
                !headerLine.toLowerCase().contains("open") ||
                !headerLine.toLowerCase().contains("high") ||
                !headerLine.toLowerCase().contains("low") ||
                !headerLine.toLowerCase().contains("close")) {
                
                logger.severe("CSV file does not have expected format. Header: " + headerLine);
                return false;
            }
            
            logger.info("CSV format validation passed for: " + csvFilePath);
            return true;
            
        } catch (IOException e) {
            logger.severe("Error validating CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get CSV file statistics
     */
    public static void printCSVStatistics(String csvFilePath) {
        int totalLines = 0;
        int dataLines = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                totalLines++;
                if (totalLines > 1) { // Skip header
                    dataLines++;
                }
            }
            
            System.out.println("=== CSV File Statistics ===");
            System.out.println("File: " + csvFilePath);
            System.out.println("Total Lines: " + totalLines);
            System.out.println("Data Lines: " + dataLines);
            System.out.println("Header Lines: 1");
            
        } catch (IOException e) {
            logger.severe("Error reading CSV statistics: " + e.getMessage());
        }
    }
}