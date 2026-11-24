/** One point of the portfolio performance curve. */
public class Snapshot {

    private final int tick;
    private final double netWorth;
    private final double portfolioValue;

    public Snapshot(int tick, double netWorth, double portfolioValue) {
        this.tick = tick;
        this.netWorth = netWorth;
        this.portfolioValue = portfolioValue;
    }

    public int getTick() {
        return tick;
    }

    public double getNetWorth() {
        return netWorth;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }
}
