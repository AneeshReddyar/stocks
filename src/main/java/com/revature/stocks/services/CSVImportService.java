package com.revature.stocks.services;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] fields;
            boolean isFirstLine = true;

            while ((fields = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    logger.info("CSV Header: " + String.join(",", fields));
                    continue;
                }

                totalRecords++;
                try {
                    if (processCSVFields(fields)) {
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

        } catch (IOException | CsvValidationException e) {
            logger.severe("Error reading CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Process a single CSV row
     */
    private boolean processCSVFields(String[] fields) {
        try {
            if (fields.length < 14) {
                logger.warning("Insufficient fields in CSV line: " + String.join(",", fields));
                return false;
            }

            // Column headers in order:
            // Date,Symbol,Series,Prev Close,Open,High,Low,Last,Close,VWAP,Volume,Turnover,Trades,Deliverable Volume,%Deliverble
            // In your previous code, symbol was fields[0], series fields[1]...
            String dateStr = fields[0].trim();
            String symbol = fields[1].trim();
            String series = fields[2].trim();

            Date tradeDate = parseDate(dateStr);
            if (tradeDate == null) {
                logger.warning("Invalid date format: " + dateStr);
                return false;
            }

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
            BigDecimal deliverablePercentage = (fields.length > 14) ? parseBigDecimal(fields[14]) : null;

            // Validate essential fields
            if (symbol.isEmpty() || openPrice == null || highPrice == null ||
                lowPrice == null || closePrice == null) {
                logger.warning("Missing essential fields in line: " + String.join(",", fields));
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
            logger.severe("Error processing CSV record: " + e.getMessage());
            return false;
        }
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
            java.util.Date utilDate = DATE_FORMAT.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e1) {
            try {
                java.util.Date utilDate = ALT_DATE_FORMAT.parse(dateStr);
                return new Date(utilDate.getTime());
            } catch (ParseException e2) {
                logger.warning("Could not parse date: " + dateStr);
                return null;
            }
        }
    }

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

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] fields;
            boolean isFirstLine = true;

            while ((fields = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (!fields[1].trim().equalsIgnoreCase(targetSymbol)) {
                    continue;
                }

                totalRecords++;

                try {
                    if (processCSVFields(fields)) {
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

        } catch (IOException | CsvValidationException e) {
            logger.severe("Error reading CSV file " + csvFilePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate CSV file format
     */
    public boolean validateCSVFormat(String csvFilePath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headerLine = reader.readNext();

            if (headerLine == null) {
                logger.severe("CSV file is empty: " + csvFilePath);
                return false;
            }

            // Basic validation - should have at least 14 columns
            if (headerLine.length < 14) {
                logger.severe("CSV file has insufficient columns. Expected at least 14, found: " + headerLine.length);
                return false;
            }

            // Check a few sample lines
            int sampleLines = 0;
            String[] fields;
            while ((fields = reader.readNext()) != null && sampleLines < 5) {
                if (fields.length < 14) {
                    logger.warning("Sample line has insufficient fields: " + String.join(",", fields));
                }
                sampleLines++;
            }

            logger.info("CSV format validation completed successfully");
            return true;

        } catch (IOException | CsvValidationException e) {
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

            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                String[] fields;
                int totalLines = 0;
                boolean isFirstLine = true;

                while ((fields = reader.readNext()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        stats.append("Header: ").append(String.join(",", fields)).append("\n");
                        continue;
                    }
                    totalLines++;
                }

                stats.append("Total Data Lines: ").append(totalLines).append("\n");
            }

            return stats.toString();

        } catch (IOException | CsvValidationException e) {
            logger.severe("Error getting import statistics: " + e.getMessage());
            return "Error getting statistics for: " + csvFilePath;
        }
    }
}
