/**
 * A single tradable stock in the simulated market.
 */
public class Stock {

    private final String symbol;
    private final String companyName;
    private double price;
    private double previousClose;

    public Stock(String symbol, String companyName, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.previousClose = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    /** Absolute change from the previous close. */
    public double getChange() {
        return price - previousClose;
    }

    /** Percentage change from the previous close. */
    public double getChangePercent() {
        if (previousClose == 0) return 0.0;
        return (getChange() / previousClose) * 100.0;
    }

    /** Applies a new market price, remembering the old one as previous close. */
    public void updatePrice(double newPrice) {
        this.previousClose = this.price;
        this.price = Math.max(1.0, round2(newPrice));
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-24s %10.2f %9.2f %8.2f%%   %s",
                symbol, companyName, price, getChange(), getChangePercent(),
                getChange() >= 0 ? "^ UP" : "v DOWN");
    }
}
