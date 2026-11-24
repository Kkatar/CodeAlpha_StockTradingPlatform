/**
 * A position held by a user in one stock (quantity + weighted average buy price).
 */
public class Holding {

    private final String symbol;
    private int quantity;
    private double totalCost;

    public Holding(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Weighted average purchase price of the shares currently held. */
    public double getAveragePrice() {
        return quantity == 0 ? 0.0 : totalCost / quantity;
    }

    public void add(int qty, double price) {
        quantity += qty;
        totalCost += qty * price;
    }

    /** Removes qty shares. Average cost of the remaining shares is preserved. */
    public void remove(int qty) {
        if (qty >= quantity) {
            quantity = 0;
            totalCost = 0;
        } else {
            totalCost -= getAveragePrice() * qty;
            quantity -= qty;
        }
    }

    /** Restores the total cost directly when loading persisted data. */
    void forceCost(double cost) {
        this.totalCost = cost;
    }

    public double getInvestedValue() {
        return Stock.round2(totalCost);
    }

    public double getMarketValue(double currentPrice) {
        return Stock.round2(quantity * currentPrice);
    }

    public double getProfitLoss(double currentPrice) {
        return Stock.round2(getMarketValue(currentPrice) - getInvestedValue());
    }
}
