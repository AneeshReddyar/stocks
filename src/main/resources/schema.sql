-- RevStox Database Schema

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS revstox_db;
USE revstox_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS stock_analytics;
DROP TABLE IF EXISTS daily_prices;
DROP TABLE IF EXISTS stocks;

-- Create stocks table
CREATE TABLE stocks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(50) NOT NULL UNIQUE,
    company_name VARCHAR(200),
    sector VARCHAR(100),
    market_cap DECIMAL(20, 2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_symbol (symbol)
);

-- Create daily_prices table
CREATE TABLE daily_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(50) NOT NULL,
    trade_date DATE NOT NULL,
    series VARCHAR(10),
    prev_close DECIMAL(10, 2),
    open_price DECIMAL(10, 2) NOT NULL,
    high_price DECIMAL(10, 2) NOT NULL,
    low_price DECIMAL(10, 2) NOT NULL,
    last_price DECIMAL(10, 2),
    close_price DECIMAL(10, 2) NOT NULL,
    vwap DECIMAL(10, 2),
    volume BIGINT,
    turnover DECIMAL(20, 2),
    trades INT,
    deliverable_volume BIGINT,
    deliverable_percentage DECIMAL(5, 4),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (symbol) REFERENCES stocks(symbol) ON DELETE CASCADE,
    UNIQUE KEY unique_symbol_date (symbol, trade_date),
    INDEX idx_symbol_date (symbol, trade_date),
    INDEX idx_trade_date (trade_date),
    INDEX idx_volume (volume)
);

-- Create stock_analytics table
CREATE TABLE stock_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(50) NOT NULL,
    analysis_date DATE NOT NULL,
    daily_volatility DECIMAL(8, 4),
    daily_price_change DECIMAL(8, 4),
    price_gap DECIMAL(10, 2),
    moving_avg_7 DECIMAL(10, 2),
    moving_avg_30 DECIMAL(10, 2),
    moving_avg_90 DECIMAL(10, 2),
    volume_trend DECIMAL(8, 4),
    turnover_ratio DECIMAL(8, 4),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (symbol) REFERENCES stocks(symbol) ON DELETE CASCADE,
    UNIQUE KEY unique_symbol_analysis_date (symbol, analysis_date),
    INDEX idx_symbol_analysis_date (symbol, analysis_date),
    INDEX idx_analysis_date (analysis_date),
    INDEX idx_volatility (daily_volatility)
);

-- Create indexes for better performance
CREATE INDEX idx_daily_prices_close ON daily_prices(close_price);
CREATE INDEX idx_daily_prices_volume ON daily_prices(volume);
CREATE INDEX idx_stock_analytics_volatility ON stock_analytics(daily_volatility);

-- Insert sample stock data
INSERT INTO stocks (symbol, company_name, sector, market_cap) VALUES
('RELIANCE', 'Reliance Industries Limited', 'Oil & Gas', 1500000.00),
('TCS', 'Tata Consultancy Services Limited', 'Information Technology', 1200000.00),
('HDFCBANK', 'HDFC Bank Limited', 'Financial Services', 800000.00),
('INFY', 'Infosys Limited', 'Information Technology', 700000.00),
('ICICIBANK', 'ICICI Bank Limited', 'Financial Services', 600000.00),
('SBIN', 'State Bank of India', 'Financial Services', 500000.00),
('HDFC', 'Housing Development Finance Corporation Limited', 'Financial Services', 450000.00),
('ITC', 'ITC Limited', 'FMCG', 400000.00),
('KOTAKBANK', 'Kotak Mahindra Bank Limited', 'Financial Services', 350000.00),
('HINDUNILVR', 'Hindustan Unilever Limited', 'FMCG', 300000.00),
('BAJFINANCE', 'Bajaj Finance Limited', 'Financial Services', 250000.00),
('AXISBANK', 'Axis Bank Limited', 'Financial Services', 220000.00),
('LT', 'Larsen & Toubro Limited', 'Construction', 200000.00),
('ASIANPAINT', 'Asian Paints Limited', 'Paints', 180000.00),
('MARUTI', 'Maruti Suzuki India Limited', 'Automobile', 170000.00),
('TATASTEEL', 'Tata Steel Limited', 'Steel', 150000.00),
('NTPC', 'NTPC Limited', 'Power', 140000.00),
('TECHM', 'Tech Mahindra Limited', 'Information Technology', 130000.00),
('POWERGRID', 'Power Grid Corporation of India Limited', 'Power', 120000.00),
('NESTLEIND', 'Nestle India Limited', 'FMCG', 110000.00),
('COALINDIA', 'Coal India Limited', 'Mining', 100000.00),
('ULTRACEMCO', 'UltraTech Cement Limited', 'Cement', 95000.00),
('ONGC', 'Oil and Natural Gas Corporation Limited', 'Oil & Gas', 90000.00),
('JSWSTEEL', 'JSW Steel Limited', 'Steel', 85000.00),
('TATAMOTORS', 'Tata Motors Limited', 'Automobile', 80000.00),
('HCLTECH', 'HCL Technologies Limited', 'Information Technology', 75000.00),
('INDUSINDBK', 'IndusInd Bank Limited', 'Financial Services', 70000.00),
('BAJAJ-AUTO', 'Bajaj Auto Limited', 'Automobile', 65000.00),
('BRITANNIA', 'Britannia Industries Limited', 'FMCG', 60000.00),
('GRASIM', 'Grasim Industries Limited', 'Cement', 55000.00);

-- ON DUPLICATE KEY UPDATE for stocks if needed
INSERT INTO stocks (symbol, company_name, sector, market_cap) VALUES
('MUNDRAPORT', 'Adani Ports and Special Economic Zone Limited', 'Infrastructure', 120000.00),
('ADANIPORTS', 'Adani Ports and Special Economic Zone Limited', 'Infrastructure', 120000.00),
('BHARTIARTL', 'Bharti Airtel Limited', 'Telecom', 400000.00),
('BPCL', 'Bharat Petroleum Corporation Limited', 'Oil & Gas', 75000.00),
('CIPLA', 'Cipla Limited', 'Pharmaceuticals', 65000.00),
('DRREDDY', 'Dr. Reddys Laboratories Limited', 'Pharmaceuticals', 85000.00),
('EICHERMOT', 'Eicher Motors Limited', 'Automobile', 45000.00),
('GAIL', 'GAIL (India) Limited', 'Oil & Gas', 55000.00),
('HEROMOTOCO', 'Hero MotoCorp Limited', 'Automobile', 35000.00),
('HINDALCO', 'Hindalco Industries Limited', 'Metals', 40000.00),
('IOC', 'Indian Oil Corporation Limited', 'Oil & Gas', 95000.00),
('SUNPHARMA', 'Sun Pharmaceutical Industries Limited', 'Pharmaceuticals', 110000.00),
('TITAN', 'Titan Company Limited', 'Consumer Durables', 95000.00),
('UPL', 'UPL Limited', 'Chemicals', 35000.00),
('VEDL', 'Vedanta Limited', 'Metals', 30000.00),
('WIPRO', 'Wipro Limited', 'Information Technology', 85000.00),
('ZEEL', 'Zee Entertainment Enterprises Limited', 'Media', 25000.00)
ON DUPLICATE KEY UPDATE
    company_name = VALUES(company_name),
    sector = VALUES(sector),
    market_cap = VALUES(market_cap);

COMMIT;