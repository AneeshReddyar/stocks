package com.revature.stocks;

import java.util.Scanner;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.revature.stocks.controllers.AnalyticsController;
import com.revature.stocks.controllers.StockController;

@SpringBootApplication
public class StocksApplication implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(StocksApplication.class.getName());
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SpringApplication.run(StocksApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting RevStox Application...");
        displayWelcomeMessage();
        showMainMenu();
    }

    private void displayWelcomeMessage() {
        System.out.println("=".repeat(60));
        System.out.println("            WELCOME TO REVSTOX");
        System.out.println("        Stock Analytics Platform");
        System.out.println("=".repeat(60));
        System.out.println();
    }

    private void showMainMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n=== REVSTOX MAIN MENU ===");
            System.out.println("1. Stock Management");
            System.out.println("2. Analytics Dashboard");
            System.out.println("3. System Information");
            System.out.println("0. Exit Application");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        openStockManagement();
                        break;
                    case 2:
                        openAnalyticsDashboard();
                        break;
                    case 3:
                        showSystemInformation();
                        break;
                    case 0:
                        exit = true;
                        displayExitMessage();
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.severe("Error in main menu: " + e.getMessage());
            }
        }
    }

    private void openStockManagement() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("          STOCK MANAGEMENT MODULE");
        System.out.println("=".repeat(50));
        
        StockController stockController = new StockController();
        stockController.displayStockMenu();
    }

    private void openAnalyticsDashboard() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("          ANALYTICS DASHBOARD");
        System.out.println("=".repeat(50));
        
        AnalyticsController analyticsController = new AnalyticsController();
        analyticsController.displayAnalyticsMenu();
        analyticsController.close();
    }

    private void showSystemInformation() {
        System.out.println("\n=== SYSTEM INFORMATION ===");
        System.out.println("Application: RevStox");
        System.out.println("Version: 1.0.0");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        System.out.println("Free Memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB");
        System.out.println("Database URL: jdbc:mysql://localhost:3306/revstox_db");
        
        System.out.println("\n=== FEATURES AVAILABLE ===");
        System.out.println("✓ Stock Data Management");
        System.out.println("✓ CSV Data Import");
        System.out.println("✓ Price History Tracking");
        System.out.println("✓ Analytics Calculations");
        System.out.println("✓ Volatility Analysis");
        System.out.println("✓ Moving Averages");
        System.out.println("✓ Performance Comparison");
        System.out.println("✓ Top Performers Ranking");
    }

    private void displayExitMessage() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        Thank you for using RevStox!");
        System.out.println("              See you again!");
        System.out.println("=".repeat(50));
        logger.info("RevStox Application terminated successfully.");
    }
}