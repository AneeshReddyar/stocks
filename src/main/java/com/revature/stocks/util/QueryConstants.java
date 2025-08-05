package com.revature.stocks.util;

/**
 * Query Constants Class
 * Contains all SQL queries used in the application
 */
public class QueryConstants {
    
    // Stock table queries
    public static final String INSERT_STOCK = 
        "INSERT INTO stocks (symbol, company_name, sector, market_cap) VALUES (?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE company_name = VALUES(company_name), sector = VALUES(sector), market_cap = VALUES(market_cap)";
    
    public static final String SELECT_STOCK_BY_SYMBOL = 
        "SELECT * FROM stocks WHERE symbol = ?";
    
    public static final String SELECT_ALL_STOCKS = 
        "SELECT * FROM stocks ORDER BY symbol";
    
    public static final String UPDATE_STOCK = 
        "UPDATE stocks SET company_name = ?, sector = ?, market_cap = ? WHERE symbol = ?";
    
    public static final String DELETE_STOCK = 
        "DELETE FROM stocks WHERE symbol = ?";
    
    // Daily prices table queries
    public static final String INSERT_DAILY_PRICE = 
        "INSERT INTO daily_prices (symbol, trade_date, series, prev_close, open_price, high_price, " +
        "low_price, last_price, close_price, vwap, volume, turnover, trades, deliverable_volume, deliverable_percentage) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "prev_close = VALUES(prev_close), open_price = VALUES(open_price), high_price = VALUES(high_price), " +
        "low_price = VALUES(low_price), last_price = VALUES(last_price), close_price = VALUES(close_price), " +
        "vwap = VALUES(vwap), volume = VALUES(volume), turnover = VALUES(turnover), trades = VALUES(trades), " +
        "deliverable_volume = VALUES(deliverable_volume), deliverable_percentage = VALUES(deliverable_percentage)";
    
    public static final String SELECT_DAILY_PRICE_BY_SYMBOL_DATE = 
        "SELECT * FROM daily_prices WHERE symbol = ? AND trade_date = ?";
    
    public static final String SELECT_DAILY_PRICES_BY_SYMBOL = 
        "SELECT * FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    public static final String SELECT_DAILY_PRICES_BY_DATE_RANGE = 
        "SELECT * FROM daily_prices WHERE symbol = ? AND trade_date BETWEEN ? AND ? ORDER BY trade_date DESC";
    
    public static final String SELECT_ALL_DAILY_PRICES = 
        "SELECT * FROM daily_prices ORDER BY trade_date DESC, symbol";
    
    public static final String SELECT_LATEST_PRICE_BY_SYMBOL = 
        "SELECT * FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC LIMIT 1";
    
    // Analytics queries
    public static final String CALCULATE_DAILY_VOLATILITY = 
        "SELECT symbol, trade_date, " +
        "CASE WHEN open_price > 0 THEN ((high_price - low_price) / open_price) * 100 ELSE 0 END as daily_volatility " +
        "FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    public static final String CALCULATE_DAILY_PRICE_CHANGE = 
        "SELECT symbol, trade_date, " +
        "CASE WHEN open_price > 0 THEN ((close_price - open_price) / open_price) * 100 ELSE 0 END as price_change " +
        "FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    public static final String CALCULATE_MOVING_AVERAGES = 
        "SELECT symbol, trade_date, close_price, " +
        "AVG(close_price) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) as moving_avg_7, " +
        "AVG(close_price) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 29 PRECEDING AND CURRENT ROW) as moving_avg_30, " +
        "AVG(close_price) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 89 PRECEDING AND CURRENT ROW) as moving_avg_90 " +
        "FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    public static final String CALCULATE_VWAP = 
        "SELECT symbol, trade_date, " +
        "SUM(close_price * volume) / NULLIF(SUM(volume), 0) as vwap " +
        "FROM daily_prices WHERE symbol = ? GROUP BY symbol, trade_date ORDER BY trade_date DESC";
    
    public static final String CALCULATE_PRICE_GAP = 
        "SELECT dp1.symbol, dp1.trade_date, " +
        "(dp1.open_price - dp2.close_price) as price_gap " +
        "FROM daily_prices dp1 " +
        "LEFT JOIN daily_prices dp2 ON dp1.symbol = dp2.symbol " +
        "AND dp2.trade_date = (SELECT MAX(trade_date) FROM daily_prices WHERE symbol = dp1.symbol AND trade_date < dp1.trade_date) " +
        "WHERE dp1.symbol = ? ORDER BY dp1.trade_date DESC";
    
    // Stock analytics table queries
    public static final String INSERT_STOCK_ANALYTICS = 
        "INSERT INTO stock_analytics (symbol, analysis_date, daily_volatility, daily_price_change, " +
        "price_gap, moving_avg_7, moving_avg_30, moving_avg_90, volume_trend, turnover_ratio) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "daily_volatility = VALUES(daily_volatility), daily_price_change = VALUES(daily_price_change), " +
        "price_gap = VALUES(price_gap), moving_avg_7 = VALUES(moving_avg_7), moving_avg_30 = VALUES(moving_avg_30), " +
        "moving_avg_90 = VALUES(moving_avg_90), volume_trend = VALUES(volume_trend), turnover_ratio = VALUES(turnover_ratio)";
    
    public static final String SELECT_ANALYTICS_BY_SYMBOL = 
        "SELECT * FROM stock_analytics WHERE symbol = ? ORDER BY analysis_date DESC";
    
    public static final String SELECT_ANALYTICS_BY_DATE_RANGE = 
        "SELECT * FROM stock_analytics WHERE symbol = ? AND analysis_date BETWEEN ? AND ? ORDER BY analysis_date DESC";
    
    // Comparative analysis queries
    public static final String COMPARE_STOCK_PERFORMANCE = 
        "SELECT symbol, AVG(daily_volatility) as avg_volatility, AVG(daily_price_change) as avg_price_change, " +
        "AVG(volume_trend) as avg_volume_trend " +
        "FROM stock_analytics WHERE analysis_date >= ? GROUP BY symbol ORDER BY avg_price_change DESC";
    
    public static final String RANK_STOCKS_BY_VOLUME = 
        "SELECT symbol, AVG(volume) as avg_volume, MAX(volume) as max_volume " +
        "FROM daily_prices WHERE trade_date >= ? GROUP BY symbol ORDER BY avg_volume DESC";
    
    public static final String RANK_STOCKS_BY_VOLATILITY = 
        "SELECT symbol, AVG(daily_volatility) as avg_volatility " +
        "FROM stock_analytics WHERE analysis_date >= ? GROUP BY symbol ORDER BY avg_volatility DESC";
    
    public static final String GET_TOP_PERFORMERS = 
        "SELECT symbol, AVG(daily_price_change) as avg_performance " +
        "FROM stock_analytics WHERE analysis_date >= ? GROUP BY symbol ORDER BY avg_performance DESC LIMIT ?";
    
    // Volume analysis queries
    public static final String ANALYZE_VOLUME_PATTERNS = 
        "SELECT symbol, trade_date, volume, " +
        "AVG(volume) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) as avg_volume_7, " +
        "LAG(volume, 1) OVER (PARTITION BY symbol ORDER BY trade_date) as prev_volume " +
        "FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    public static final String CALCULATE_TURNOVER_ANALYSIS = 
        "SELECT symbol, trade_date, (close_price * volume) as calculated_turnover, turnover, " +
        "CASE WHEN volume > 0 THEN (deliverable_volume / volume) * 100 ELSE 0 END as liquidity_ratio " +
        "FROM daily_prices WHERE symbol = ? ORDER BY trade_date DESC";
    
    // Date range queries
    public static final String GET_DATE_RANGE_FOR_SYMBOL = 
        "SELECT MIN(trade_date) as start_date, MAX(trade_date) as end_date " +
        "FROM daily_prices WHERE symbol = ?";
    
    public static final String GET_AVAILABLE_SYMBOLS = 
        "SELECT DISTINCT symbol FROM daily_prices ORDER BY symbol";
    
    // Cleanup queries
    public static final String DELETE_OLD_ANALYTICS = 
        "DELETE FROM stock_analytics WHERE analysis_date < ?";
    
    public static final String COUNT_RECORDS_BY_SYMBOL = 
        "SELECT symbol, COUNT(*) as record_count FROM daily_prices GROUP BY symbol ORDER BY record_count DESC";
}