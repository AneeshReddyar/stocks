package com.revature.stocks.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * CSVReader Utility Class
 * Utility methods for reading and parsing CSV files
 */
public class CSVReader {
    
    private static final Logger logger = Logger.getLogger(CSVReader.class.getName());
    
    /**
     * Read all lines from CSV file
     */
    public static List<String[]> readCSV(String filePath) {
        List<String[]> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                records.add(fields);
            }
            
            logger.info("Read " + records.size() + " lines from CSV: " + filePath);
            
        } catch (IOException e) {
            logger.severe("Error reading CSV file " + filePath + ": " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * Read CSV file with header
     */
    public static List<String[]> readCSVWithHeader(String filePath) {
        List<String[]> records = readCSV(filePath);
        
        if (!records.isEmpty()) {
            // Remove header
            records.remove(0);
            logger.info("Removed header, remaining records: " + records.size());
        }
        
        return records;
    }
    
    /**
     * Parse CSV line handling quoted values and commas
     */
    public static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Get CSV file statistics
     */
    public static String getCSVStats(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            int columnCount = 0;
            String header = null;
            
            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) {
                    header = line;
                    columnCount = parseCSVLine(line).length;
                }
                lineCount++;
            }
            
            StringBuilder stats = new StringBuilder();
            stats.append("File: ").append(filePath).append("\n");
            stats.append("Total Lines: ").append(lineCount).append("\n");
            stats.append("Data Lines: ").append(lineCount - 1).append("\n");
            stats.append("Columns: ").append(columnCount).append("\n");
            stats.append("Header: ").append(header).append("\n");
            
            return stats.toString();
            
        } catch (IOException e) {
            logger.severe("Error getting CSV stats: " + e.getMessage());
            return "Error reading file: " + filePath;
        }
    }
    
    /**
     * Validate CSV file exists and is readable
     */
    public static boolean validateCSVFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            return firstLine != null && !firstLine.trim().isEmpty();
        } catch (IOException e) {
            logger.severe("CSV file validation failed for " + filePath + ": " + e.getMessage());
            return false;
        }
    }
}