import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An investor using the platform: cash balance, holdings and transaction history.
 */
public class User {

    private final String username;
    private double balance;
    private final Map<String, Holding> portfolio = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public User(String username, double balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return Stock.round2(balance);
    }

    public Map<String, Holding> getPortfolio() {
        return portfolio;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Holding getHolding(String symbol) {
        return portfolio.get(symbol);
    }

    public int getQuantity(String symbol) {
        Holding h = portfolio.get(symbol);
        return h == null ? 0 : h.getQuantity();
    }

    // ------------------------------------------------------------------
    //  Trading operations
    // ------------------------------------------------------------------
    public boolean buy(Stock stock, int qty) {
        double cost = Stock.round2(stock.getPrice() * qty);
        if (qty <= 0 || cost > balance) {
            return false;
        }
        balance = Stock.round2(balance - cost);
        portfolio.computeIfAbsent(stock.getSymbol(), Holding::new)
                 .add(qty, stock.getPrice());
        transactions.add(new Transaction(Transaction.Type.BUY, stock.getSymbol(), qty, stock.getPrice()));
        return true;
    }

    public boolean sell(Stock stock, int qty) {
        Holding h = portfolio.get(stock.getSymbol());
        if (qty <= 0 || h == null || h.getQuantity() < qty) {
            return false;
        }
        double proceeds = Stock.round2(stock.getPrice() * qty);
        balance = Stock.round2(balance + proceeds);
        h.remove(qty);
        if (h.getQuantity() == 0) {
            portfolio.remove(stock.getSymbol());
        }
        transactions.add(new Transaction(Transaction.Type.SELL, stock.getSymbol(), qty, stock.getPrice()));
        return true;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance = Stock.round2(balance + amount);
        }
    }

    // ------------------------------------------------------------------
    //  Portfolio valuation
    // ------------------------------------------------------------------
    public double getPortfolioValue(Market market) {
        double total = 0.0;
        for (Holding h : portfolio.values()) {
            Stock s = market.getStock(h.getSymbol());
            if (s != null) {
                total += h.getMarketValue(s.getPrice());
            }
        }
        return Stock.round2(total);
    }

    public double getTotalInvested() {
        return Stock.round2(portfolio.values().stream().mapToDouble(Holding::getInvestedValue).sum());
    }

    public double getTotalProfitLoss(Market market) {
        return Stock.round2(getPortfolioValue(market) - getTotalInvested());
    }

    public double getNetWorth(Market market) {
        return Stock.round2(getBalance() + getPortfolioValue(market));
    }
}
