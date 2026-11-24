import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Simulated stock market: holds the tradable stocks and simulates price movement.
 */
public class Market {

    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random random = new Random();
    private int tick = 0;

    public Market() {
        add(new Stock("TCS",    "Tata Consultancy Services", 3850.00));
        add(new Stock("INFY",   "Infosys Ltd",               1620.00));
        add(new Stock("RELI",   "Reliance Industries",       2890.00));
        add(new Stock("HDFC",   "HDFC Bank",                 1685.00));
        add(new Stock("WIPRO",  "Wipro Ltd",                  512.00));
        add(new Stock("TATAM",  "Tata Motors",                945.00));
        add(new Stock("SBIN",   "State Bank of India",        812.00));
        add(new Stock("ITC",    "ITC Ltd",                    438.00));
    }

    private void add(Stock s) {
        stocks.put(s.getSymbol(), s);
    }

    public List<Stock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    public int getTick() {
        return tick;
    }

    /**
     * Simulates one market tick: every stock moves by a small random percentage
     * between -3% and +3%.
     */
    public void simulateTick() {
        tick++;
        for (Stock s : stocks.values()) {
            double pct = (random.nextDouble() * 6.0) - 3.0;      // -3% .. +3%
            double newPrice = s.getPrice() * (1 + pct / 100.0);
            s.updatePrice(newPrice);
        }
    }

    /** Returns the market snapshot as a formatted table. */
    public String getMarketTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n-------------------------------------------------------------------------\n");
        sb.append(String.format("                     MARKET WATCH  (tick #%d)%n", tick));
        sb.append("-------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s %-24s %10s %9s %9s %s%n",
                "SYMBOL", "COMPANY", "PRICE", "CHANGE", "CHANGE%", "TREND"));
        sb.append("-------------------------------------------------------------------------\n");
        for (Stock s : stocks.values()) {
            sb.append(" ").append(s).append("\n");
        }
        sb.append("-------------------------------------------------------------------------\n");
        return sb.toString();
    }
}
