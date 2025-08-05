package com.revature.stocks.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.revature.stocks.model.StockAnalytics;
import com.revature.stocks.services.AnalyticsService;
import com.revature.stocks.services.StockService;

/**
 * AnalyticsController Class
 * Handles analytics-related operations and user interactions
 */
public class AnalyticsController {
    
    private static final Logger logger = Logger.getLogger(AnalyticsController.class.getName());
    private AnalyticsService analyticsService;
    private StockService stockService;
    private Scanner scanner;
    
    public AnalyticsController() {
        this.analyticsService = new AnalyticsService();
        this.stockService = new StockService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Display analytics menu
     */
    public void displayAnalyticsMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n=== STOCK ANALYTICS MENU ===");
            System.out.println("1. Calculate Analytics for Stock");
            System.out.println("2. View Stock Analytics");
            System.out.println("3. Calculate Daily Volatility");
            System.out.println("4. Calculate Daily Price Changes");
            System.out.println("5. Calculate Moving Averages");
            System.out.println("6. Calculate Price Gaps");
            System.out.println("7. Compare Stock Performance");
            System.out.println("8. Get Top Performers");
            System.out.println("9. Rank Stocks by Volatility");
            System.out.println("10. Generate Analytics Summary");
            System.out.println("11. Cleanup Old Analytics");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        calculateAnalyticsForStock();
                        break;
                    case 2:
                        viewStockAnalytics();
                        break;
                    case 3:
                        calculateDailyVolatility();
                        break;
                    case 4:
                        calculateDailyPriceChanges();
                        break;
                    case 5:
                        calculateMovingAverages();
                        break;
                    case 6:
                        calculatePriceGaps();
                        break;
                    case 7:
                        compareStockPerformance();
                        break;
                    case 8:
                        getTopPerformers();
                        break;
                    case 9:
                        rankStocksByVolatility();
                        break;
                    case 10:
                        generateAnalyticsSummary();
                        break;
                    case 11:
                        cleanupOldAnalytics();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Exiting Analytics...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.severe("Error in analytics menu: " + e.getMessage());
            }
        }
    }
    
    /**
     * Calculate analytics for a specific stock
     */
    private void calculateAnalyticsForStock() {
        System.out.println("\n=== CALCULATE ANALYTICS ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        // Check if stock exists
        if (!stockService.doesStockExist(symbol)) {
            System.out.println("Stock not found: " + symbol);
            return;
        }
        
        System.out.print("Enter analysis date (YYYY-MM-DD) or press Enter for today: ");
        String dateStr = scanner.nextLine().trim();
        Date analysisDate;
        
        if (dateStr.isEmpty()) {
            analysisDate = new Date(System.currentTimeMillis());
        } else {
            try {
                analysisDate = Date.valueOf(dateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format! Using today's date.");
                analysisDate = new Date(System.currentTimeMillis());
            }
        }
        
        System.out.println("Calculating analytics for " + symbol + " on " + analysisDate + "...");
        
        boolean success = analyticsService.calculateAndStoreAnalytics(symbol, analysisDate);
        
        if (success) {
            System.out.println("Analytics calculated and stored successfully!");
        } else {
            System.out.println("Failed to calculate analytics. Check if price data exists for the specified date.");
        }
    }
    
    /**
     * View stock analytics
     */
    private void viewStockAnalytics() {
        System.out.println("\n=== VIEW ANALYTICS ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        System.out.print("Enter number of recent records to display (default 10): ");
        String limitStr = scanner.nextLine().trim();
        int limit = 10;
        
        if (!limitStr.isEmpty()) {
            try {
                limit = Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Using default limit of 10.");
            }
        }
        
        List<StockAnalytics> analytics = analyticsService.getAnalytics(symbol);
        
        if (analytics == null || analytics.isEmpty()) {
            System.out.println("No analytics data found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== ANALYTICS FOR " + symbol + " ===");
        System.out.printf("%-12s %-12s %-15s %-12s %-12s %-12s%n", 
                         "DATE", "VOLATILITY", "PRICE_CHANGE", "MA_7", "MA_30", "CATEGORY");
        System.out.println("=".repeat(85));
        
        int count = 0;
        for (StockAnalytics analytic : analytics) {
            if (count >= limit) break;
            
            System.out.printf("%-12s %-12s %-15s %-12s %-12s %-12s%n",
                             analytic.getAnalysisDate(),
                             analytic.getDailyVolatility() != null ? 
                                 String.format("%.2f%%", analytic.getDailyVolatility()) : "N/A",
                             analytic.getDailyPriceChange() != null ? 
                                 String.format("%.2f%%", analytic.getDailyPriceChange()) : "N/A",
                             analytic.getMovingAvg7() != null ? 
                                 String.format("%.2f", analytic.getMovingAvg7()) : "N/A",
                             analytic.getMovingAvg30() != null ? 
                                 String.format("%.2f", analytic.getMovingAvg30()) : "N/A",
                             analytic.getVolatilityCategory());
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + analytics.size() + " total records");
    }
    
    /**
     * Calculate daily volatility
     */
    private void calculateDailyVolatility() {
        System.out.println("\n=== DAILY VOLATILITY ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        List<Map<String, Object>> volatilityData = analyticsService.calculateDailyVolatility(symbol);
        
        if (volatilityData == null || volatilityData.isEmpty()) {
            System.out.println("No volatility data found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== DAILY VOLATILITY FOR " + symbol + " ===");
        System.out.printf("%-15s %-15s%n", "DATE", "VOLATILITY (%)");
        System.out.println("=".repeat(32));
        
        int count = 0;
        for (Map<String, Object> data : volatilityData) {
            if (count >= 20) break; // Show top 20 records
            
            System.out.printf("%-15s %-15s%n",
                             data.get("trade_date"),
                             String.format("%.4f", data.get("daily_volatility")));
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + volatilityData.size() + " total records");
    }
    
    /**
     * Calculate daily price changes
     */
    private void calculateDailyPriceChanges() {
        System.out.println("\n=== DAILY PRICE CHANGES ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        List<Map<String, Object>> priceChangeData = analyticsService.calculateDailyPriceChanges(symbol);
        
        if (priceChangeData == null || priceChangeData.isEmpty()) {
            System.out.println("No price change data found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== DAILY PRICE CHANGES FOR " + symbol + " ===");
        System.out.printf("%-15s %-18s%n", "DATE", "PRICE CHANGE (%)");
        System.out.println("=".repeat(35));
        
        int count = 0;
        for (Map<String, Object> data : priceChangeData) {
            if (count >= 20) break;
            
            System.out.printf("%-15s %-18s%n",
                             data.get("trade_date"),
                             String.format("%.4f", data.get("price_change")));
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + priceChangeData.size() + " total records");
    }
    
    /**
     * Calculate moving averages
     */
    private void calculateMovingAverages() {
        System.out.println("\n=== MOVING AVERAGES ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        List<Map<String, Object>> movingAvgData = analyticsService.calculateMovingAverages(symbol);
        
        if (movingAvgData == null || movingAvgData.isEmpty()) {
            System.out.println("No moving average data found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== MOVING AVERAGES FOR " + symbol + " ===");
        System.out.printf("%-12s %-10s %-10s %-10s %-10s%n", 
                         "DATE", "CLOSE", "MA_7", "MA_30", "MA_90");
        System.out.println("=".repeat(55));
        
        int count = 0;
        for (Map<String, Object> data : movingAvgData) {
            if (count >= 15) break;
            
            System.out.printf("%-12s %-10s %-10s %-10s %-10s%n",
                             data.get("trade_date"),
                             String.format("%.2f", data.get("close_price")),
                             data.get("moving_avg_7") != null ? 
                                 String.format("%.2f", data.get("moving_avg_7")) : "N/A",
                             data.get("moving_avg_30") != null ? 
                                 String.format("%.2f", data.get("moving_avg_30")) : "N/A",
                             data.get("moving_avg_90") != null ? 
                                 String.format("%.2f", data.get("moving_avg_90")) : "N/A");
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + movingAvgData.size() + " total records");
    }
    
    /**
     * Calculate price gaps
     */
    private void calculatePriceGaps() {
        System.out.println("\n=== PRICE GAPS ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        List<Map<String, Object>> priceGapData = analyticsService.calculatePriceGaps(symbol);
        
        if (priceGapData == null || priceGapData.isEmpty()) {
            System.out.println("No price gap data found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== PRICE GAPS FOR " + symbol + " ===");
        System.out.printf("%-15s %-15s%n", "DATE", "PRICE GAP");
        System.out.println("=".repeat(32));
        
        int count = 0;
        for (Map<String, Object> data : priceGapData) {
            if (count >= 20) break;
            
            System.out.printf("%-15s %-15s%n",
                             data.get("trade_date"),
                             String.format("%.4f", data.get("price_gap")));
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + priceGapData.size() + " total records");
    }
    
    /**
     * Compare stock performance
     */
    private void compareStockPerformance() {
        System.out.println("\n=== COMPARE STOCK PERFORMANCE ===");
        
        System.out.print("Enter comparison start date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        if (dateStr.isEmpty()) {
            System.out.println("Date cannot be empty!");
            return;
        }
        
        Date fromDate;
        try {
            fromDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            return;
        }
        
        List<Map<String, Object>> performanceData = analyticsService.compareStockPerformance(fromDate);
        
        if (performanceData == null || performanceData.isEmpty()) {
            System.out.println("No performance data found from: " + fromDate);
            return;
        }
        
        System.out.println("\n=== STOCK PERFORMANCE COMPARISON FROM " + fromDate + " ===");
        System.out.printf("%-10s %-15s %-15s %-15s%n", 
                         "SYMBOL", "AVG_VOLATILITY", "AVG_CHANGE", "PERFORMANCE");
        System.out.println("=".repeat(65));
        
        for (Map<String, Object> data : performanceData) {
            System.out.printf("%-10s %-15s %-15s %-15s%n",
                             data.get("symbol"),
                             String.format("%.4f%%", data.get("avg_volatility")),
                             String.format("%.4f%%", data.get("avg_price_change")),
                             data.get("performance_category"));
        }
        
        System.out.println("\nTotal stocks compared: " + performanceData.size());
    }
    
    /**
     * Get top performing stocks
     */
    private void getTopPerformers() {
        System.out.println("\n=== TOP PERFORMERS ===");
        
        System.out.print("Enter analysis start date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        if (dateStr.isEmpty()) {
            System.out.println("Date cannot be empty!");
            return;
        }
        
        Date fromDate;
        try {
            fromDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            return;
        }
        
        System.out.print("Enter number of top performers to display (default 10): ");
        String limitStr = scanner.nextLine().trim();
        int limit = 10;
        
        if (!limitStr.isEmpty()) {
            try {
                limit = Integer.parseInt(limitStr);
                if (limit <= 0) limit = 10;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Using default limit of 10.");
            }
        }
        
        List<Map<String, Object>> topPerformers = analyticsService.getTopPerformers(fromDate, limit);
        
        if (topPerformers == null || topPerformers.isEmpty()) {
            System.out.println("No performance data found from: " + fromDate);
            return;
        }
        
        System.out.println("\n=== TOP " + limit + " PERFORMERS FROM " + fromDate + " ===");
        System.out.printf("%-5s %-10s %-15s %-15s %-15s%n", 
                         "RANK", "SYMBOL", "AVG_RETURN", "VOLATILITY", "CATEGORY");
        System.out.println("=".repeat(70));
        
        int rank = 1;
        for (Map<String, Object> data : topPerformers) {
            System.out.printf("%-5d %-10s %-15s %-15s %-15s%n",
                             rank++,
                             data.get("symbol"),
                             String.format("%.4f%%", data.get("avg_return")),
                             String.format("%.4f%%", data.get("avg_volatility")),
                             data.get("performance_category"));
        }
        
        System.out.println("\nDisplaying top " + topPerformers.size() + " performers");
    }
    
    /**
     * Rank stocks by volatility
     */
    private void rankStocksByVolatility() {
        System.out.println("\n=== VOLATILITY RANKING ===");
        
        System.out.print("Enter analysis start date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        if (dateStr.isEmpty()) {
            System.out.println("Date cannot be empty!");
            return;
        }
        
        Date fromDate;
        try {
            fromDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            return;
        }
        
        List<Map<String, Object>> volatilityRanking = analyticsService.rankStocksByVolatility(fromDate);
        
        if (volatilityRanking == null || volatilityRanking.isEmpty()) {
            System.out.println("No volatility data found from: " + fromDate);
            return;
        }
        
        System.out.println("\n=== VOLATILITY RANKING FROM " + fromDate + " ===");
        System.out.printf("%-5s %-10s %-15s %-15s %-15s%n", 
                         "RANK", "SYMBOL", "AVG_VOLATILITY", "MAX_VOLATILITY", "CATEGORY");
        System.out.println("=".repeat(70));
        
        int rank = 1;
        for (Map<String, Object> data : volatilityRanking) {
            System.out.printf("%-5d %-10s %-15s %-15s %-15s%n",
                             rank++,
                             data.get("symbol"),
                             String.format("%.4f%%", data.get("avg_volatility")),
                             String.format("%.4f%%", data.get("max_volatility")),
                             data.get("volatility_category"));
        }
        
        System.out.println("\nTotal stocks ranked: " + volatilityRanking.size());
    }
    
    /**
     * Generate analytics summary
     */
    private void generateAnalyticsSummary() {
        System.out.println("\n=== GENERATE ANALYTICS SUMMARY ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        // Check if stock exists
        if (!stockService.doesStockExist(symbol)) {
            System.out.println("Stock not found: " + symbol);
            return;
        }
        
        System.out.print("Enter analysis start date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        if (dateStr.isEmpty()) {
            System.out.println("Date cannot be empty!");
            return;
        }
        
        Date fromDate;
        try {
            fromDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            return;
        }
        
        System.out.println("\nGenerating analytics summary for " + symbol + " from " + fromDate + "...\n");
        
        String summary = analyticsService.generateAnalyticsSummary(symbol, fromDate);
        System.out.println(summary);
    }
    
    /**
     * Clean up old analytics data
     */
    private void cleanupOldAnalytics() {
        System.out.println("\n=== CLEANUP OLD ANALYTICS ===");
        
        System.out.print("Enter cutoff date (YYYY-MM-DD) - analytics before this date will be deleted: ");
        String dateStr = scanner.nextLine().trim();
        
        if (dateStr.isEmpty()) {
            System.out.println("Date cannot be empty!");
            return;
        }
        
        Date beforeDate;
        try {
            beforeDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            return;
        }
        
        System.out.print("Are you sure you want to delete all analytics data before " + beforeDate + "? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("y") && !confirmation.equals("yes")) {
            System.out.println("Cleanup cancelled.");
            return;
        }
        
        System.out.println("Cleaning up old analytics data...");
        
        int deletedRecords = analyticsService.cleanupOldAnalytics(beforeDate);
        
        if (deletedRecords > 0) {
            System.out.println("Successfully deleted " + deletedRecords + " old analytics records.");
        } else {
            System.out.println("No records were deleted. Either no old records exist or an error occurred.");
        }
    }
    
    /**
     * Close scanner resources
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}