package com.revature.stocks.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.revature.stocks.model.DailyPrice;
import com.revature.stocks.model.Stock;
import com.revature.stocks.services.CSVImportService;
import com.revature.stocks.services.StockService;

/**
 * StockController Class
 * Handles stock-related operations and user interactions
 */
public class StockController {
    
    private static final Logger logger = Logger.getLogger(StockController.class.getName());
    private StockService stockService;
    private CSVImportService csvImportService;
    private Scanner scanner;
    
    public StockController() {
        this.stockService = new StockService();
        this.csvImportService = new CSVImportService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Display main stock menu
     */
    public void displayStockMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n=== STOCK MANAGEMENT MENU ===");
            System.out.println("1. Import CSV Data");
            System.out.println("2. View All Stocks");
            System.out.println("3. Search Stock by Symbol");
            System.out.println("4. Add New Stock");
            System.out.println("5. Update Stock Information");
            System.out.println("6. Delete Stock");
            System.out.println("7. Get Latest Price");
            System.out.println("8. Get Price History");
            System.out.println("9. Get Stock Summary");
            System.out.println("10. Get Available Symbols");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        handleCSVImport();
                        break;
                    case 2:
                        viewAllStocks();
                        break;
                    case 3:
                        searchStockBySymbol();
                        break;
                    case 4:
                        addNewStock();
                        break;
                    case 5:
                        updateStockInformation();
                        break;
                    case 6:
                        deleteStock();
                        break;
                    case 7:
                        getLatestPrice();
                        break;
                    case 8:
                        getPriceHistory();
                        break;
                    case 9:
                        getStockSummary();
                        break;
                    case 10:
                        getAvailableSymbols();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Exiting Stock Management...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.severe("Error in stock menu: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle CSV import
     */
    private void handleCSVImport() {
        System.out.println("\n=== CSV DATA IMPORT ===");
        System.out.print("Enter CSV file path (or press Enter for default): ");
        String filePath = scanner.nextLine().trim();
        
        if (filePath.isEmpty()) {
            filePath = "src/main/resources/data/NIFTY50_data.csv";
        }
        
        System.out.println("Starting CSV import from: " + filePath);
        
        // Validate CSV file first
        if (!csvImportService.validateCSVFormat(filePath)) {
            System.out.println("CSV file validation failed!");
            return;
        }
        
        // Show file statistics
        System.out.println(csvImportService.getImportStatistics(filePath));
        
        System.out.print("Do you want to proceed with import? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            long startTime = System.currentTimeMillis();
            boolean success = csvImportService.importDataFromCSV(filePath);
            long endTime = System.currentTimeMillis();
            
            if (success) {
                System.out.println("CSV import completed successfully!");
                System.out.println("Import time: " + (endTime - startTime) + " ms");
            } else {
                System.out.println("CSV import completed with some errors. Check logs for details.");
            }
        } else {
            System.out.println("CSV import cancelled.");
        }
    }
    
    /**
     * View all stocks
     */
    private void viewAllStocks() {
        System.out.println("\n=== ALL STOCKS ===");
        
        List<Stock> stocks = stockService.getAllStocks();
        
        if (stocks == null || stocks.isEmpty()) {
            System.out.println("No stocks found in the database.");
            return;
        }
        
        System.out.printf("%-15s %-40s %-20s %-15s%n", 
                         "SYMBOL", "COMPANY NAME", "SECTOR", "MARKET CAP");
        System.out.println("=".repeat(90));
        
        for (Stock stock : stocks) {
            System.out.printf("%-15s %-40s %-20s %-15s%n",
                             stock.getSymbol(),
                             stock.getCompanyName() != null ? stock.getCompanyName() : "N/A",
                             stock.getSector() != null ? stock.getSector() : "N/A",
                             stock.getMarketCap() != null ? stock.getMarketCap().toString() : "N/A");
        }
        
        System.out.println("\nTotal stocks: " + stocks.size());
    }
    
    /**
     * Search stock by symbol
     */
    private void searchStockBySymbol() {
        System.out.println("\n=== SEARCH STOCK ===");
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        Stock stock = stockService.getStockBySymbol(symbol);
        
        if (stock == null) {
            System.out.println("Stock not found: " + symbol);
        } else {
            System.out.println("\n=== STOCK DETAILS ===");
            System.out.println("Symbol: " + stock.getSymbol());
            System.out.println("Company Name: " + (stock.getCompanyName() != null ? stock.getCompanyName() : "N/A"));
            System.out.println("Sector: " + (stock.getSector() != null ? stock.getSector() : "N/A"));
            System.out.println("Market Cap: " + (stock.getMarketCap() != null ? stock.getMarketCap() : "N/A"));
            System.out.println("Created Date: " + stock.getCreatedDate());
            System.out.println("Updated Date: " + stock.getUpdatedDate());
        }
    }
    
    /**
     * Add new stock
     */
    private void addNewStock() {
        System.out.println("\n=== ADD NEW STOCK ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        // Check if stock already exists
        if (stockService.doesStockExist(symbol)) {
            System.out.println("Stock already exists: " + symbol);
            return;
        }
        
        System.out.print("Enter company name: ");
        String companyName = scanner.nextLine().trim();
        
        System.out.print("Enter sector: ");
        String sector = scanner.nextLine().trim();
        
        System.out.print("Enter market cap (or press Enter to skip): ");
        String marketCapStr = scanner.nextLine().trim();
        BigDecimal marketCap = null;
        
        if (!marketCapStr.isEmpty()) {
            try {
                marketCap = new BigDecimal(marketCapStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid market cap format. Setting to null.");
            }
        }
        
        boolean success = stockService.addOrUpdateStock(symbol, companyName, sector, marketCap);
        
        if (success) {
            System.out.println("Stock added successfully: " + symbol);
        } else {
            System.out.println("Failed to add stock: " + symbol);
        }
    }
    
    /**
     * Update stock information
     */
    private void updateStockInformation() {
        System.out.println("\n=== UPDATE STOCK ===");
        
        System.out.print("Enter stock symbol to update: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        // Check if stock exists
        Stock existingStock = stockService.getStockBySymbol(symbol);
        if (existingStock == null) {
            System.out.println("Stock not found: " + symbol);
            return;
        }
        
        System.out.println("Current information:");
        System.out.println("Company Name: " + existingStock.getCompanyName());
        System.out.println("Sector: " + existingStock.getSector());
        System.out.println("Market Cap: " + existingStock.getMarketCap());
        
        System.out.print("Enter new company name (or press Enter to keep current): ");
        String companyName = scanner.nextLine().trim();
        if (companyName.isEmpty()) {
            companyName = existingStock.getCompanyName();
        }
        
        System.out.print("Enter new sector (or press Enter to keep current): ");
        String sector = scanner.nextLine().trim();
        if (sector.isEmpty()) {
            sector = existingStock.getSector();
        }
        
        System.out.print("Enter new market cap (or press Enter to keep current): ");
        String marketCapStr = scanner.nextLine().trim();
        BigDecimal marketCap = existingStock.getMarketCap();
        
        if (!marketCapStr.isEmpty()) {
            try {
                marketCap = new BigDecimal(marketCapStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid market cap format. Keeping current value.");
            }
        }
        
        boolean success = stockService.updateStockInfo(symbol, companyName, sector, marketCap);
        
        if (success) {
            System.out.println("Stock updated successfully: " + symbol);
        } else {
            System.out.println("Failed to update stock: " + symbol);
        }
    }
    
    /**
     * Delete stock
     */
    private void deleteStock() {
        System.out.println("\n=== DELETE STOCK ===");
        
        System.out.print("Enter stock symbol to delete: ");
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
        
        System.out.print("Are you sure you want to delete " + symbol + "? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean success = stockService.deleteStock(symbol);
            
            if (success) {
                System.out.println("Stock deleted successfully: " + symbol);
            } else {
                System.out.println("Failed to delete stock: " + symbol);
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }
    
    /**
     * Get latest price for a stock
     */
    private void getLatestPrice() {
        System.out.println("\n=== LATEST PRICE ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        DailyPrice latestPrice = stockService.getLatestPrice(symbol);
        
        if (latestPrice == null) {
            System.out.println("No price data found for: " + symbol);
        } else {
            System.out.println("\n=== LATEST PRICE FOR " + symbol + " ===");
            System.out.println("Date: " + latestPrice.getTradeDate());
            System.out.println("Open: " + latestPrice.getOpenPrice());
            System.out.println("High: " + latestPrice.getHighPrice());
            System.out.println("Low: " + latestPrice.getLowPrice());
            System.out.println("Close: " + latestPrice.getClosePrice());
            System.out.println("Volume: " + latestPrice.getVolume());
            System.out.println("VWAP: " + latestPrice.getVwap());
            System.out.println("Turnover: " + latestPrice.getTurnover());
        }
    }
    
    /**
     * Get price history for a stock
     */
    private void getPriceHistory() {
        System.out.println("\n=== PRICE HISTORY ===");
        
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
                System.out.println("Invalid number format. Using default limit of 10.");
            }
        }
        
        List<DailyPrice> priceHistory = stockService.getPriceHistory(symbol);
        
        if (priceHistory == null || priceHistory.isEmpty()) {
            System.out.println("No price history found for: " + symbol);
            return;
        }
        
        System.out.println("\n=== PRICE HISTORY FOR " + symbol + " ===");
        System.out.printf("%-12s %-10s %-10s %-10s %-10s %-12s%n", 
                         "DATE", "OPEN", "HIGH", "LOW", "CLOSE", "VOLUME");
        System.out.println("=".repeat(70));
        
        int count = 0;
        for (DailyPrice price : priceHistory) {
            if (count >= limit) break;
            
            System.out.printf("%-12s %-10s %-10s %-10s %-10s %-12s%n",
                             price.getTradeDate(),
                             price.getOpenPrice(),
                             price.getHighPrice(),
                             price.getLowPrice(),
                             price.getClosePrice(),
                             price.getVolume() != null ? price.getVolume() : "N/A");
            count++;
        }
        
        System.out.println("\nShowing " + count + " of " + priceHistory.size() + " total records");
    }
    
    /**
     * Get stock summary
     */
    private void getStockSummary() {
        System.out.println("\n=== STOCK SUMMARY ===");
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty!");
            return;
        }
        
        String summary = stockService.getStockSummary(symbol);
        System.out.println(summary);
    }
    
    /**
     * Get available symbols
     */
    private void getAvailableSymbols() {
        System.out.println("\n=== AVAILABLE SYMBOLS ===");
        
        List<String> symbols = stockService.getAvailableSymbols();
        
        if (symbols == null || symbols.isEmpty()) {
            System.out.println("No symbols found in the database.");
            return;
        }
        
        System.out.println("Available stock symbols:");
        int count = 0;
        for (String symbol : symbols) {
            System.out.print(symbol + "\t");
            count++;
            if (count % 5 == 0) {
                System.out.println();
            }
        }
        
        if (count % 5 != 0) {
            System.out.println();
        }
        
        System.out.println("\nTotal symbols: " + symbols.size());
    }
}