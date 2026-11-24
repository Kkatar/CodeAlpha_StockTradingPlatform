import java.util.List;
import java.util.Scanner;

/**
 * Task 2 - Stock Trading Platform
 *
 * Simulates a basic stock trading environment:
 *   - live market data display with simulated price movement
 *   - buy / sell operations with cash balance validation
 *   - portfolio tracking and performance history over time
 *   - OOP based design (Stock, User, Holding, Transaction, Market)
 *   - file I/O persistence for account, holdings, transactions and performance
 */
public class StockTradingPlatform {

    private static final double STARTING_CASH = 100000.00;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Market market = new Market();
        PortfolioManager manager = new PortfolioManager();
        User user = manager.loadOrCreate(STARTING_CASH);
        PortfolioHistory history = new PortfolioHistory();

        history.record(market.getTick(), user, market);

        System.out.println("=========================================================");
        System.out.println("        STOCK TRADING PLATFORM  (CodeAlpha)              ");
        System.out.println("=========================================================");
        System.out.println(" Welcome, " + user.getUsername() + "!  Cash balance: " + money(user.getBalance()));
        if (!user.getTransactions().isEmpty()) {
            System.out.println(" Loaded saved portfolio: " + user.getPortfolio().size()
                    + " holding(s), " + user.getTransactions().size() + " past transaction(s).");
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: System.out.println(market.getMarketTable());        break;
                case 2: buyStock(market, user, history);                    break;
                case 3: sellStock(market, user, history);                   break;
                case 4: viewPortfolio(market, user);                        break;
                case 5: viewTransactions(user);                             break;
                case 6: System.out.println(history.buildChart());           break;
                case 7:
                    market.simulateTick();
                    history.record(market.getTick(), user, market);
                    System.out.println("\n Market moved to tick #" + market.getTick() + ". Prices updated.");
                    System.out.println(market.getMarketTable());
                    break;
                case 8: depositCash(user);                                  break;
                case 9:
                    manager.save(user, history);
                    running = false;
                    System.out.println("\n Portfolio saved to the data/ folder. Happy investing, goodbye!");
                    break;
                default:
                    System.out.println(" Invalid choice. Please select 1-9.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n------------------------ MENU ------------------------");
        System.out.println(" 1. View Market Data");
        System.out.println(" 2. Buy Stock");
        System.out.println(" 3. Sell Stock");
        System.out.println(" 4. View Portfolio");
        System.out.println(" 5. Transaction History");
        System.out.println(" 6. Portfolio Performance Chart");
        System.out.println(" 7. Next Market Tick (simulate price change)");
        System.out.println(" 8. Add Funds");
        System.out.println(" 9. Save & Exit");
        System.out.println("------------------------------------------------------");
    }

    // ---------------------------------------------------------------
    //  Trading
    // ---------------------------------------------------------------
    private static void buyStock(Market market, User user, PortfolioHistory history) {
        System.out.println("\n--- BUY STOCK ---");
        System.out.println(market.getMarketTable());
        Stock stock = askStock(market);
        if (stock == null) return;

        int qty = readInt(" Quantity to buy: ");
        if (qty <= 0) {
            System.out.println(" Quantity must be greater than zero.");
            return;
        }
        double cost = Stock.round2(stock.getPrice() * qty);
        System.out.println(" " + qty + " x " + stock.getSymbol() + " @ " + money(stock.getPrice())
                + "  =  " + money(cost));
        System.out.println(" Available cash: " + money(user.getBalance()));

        if (cost > user.getBalance()) {
            System.out.println(" Insufficient funds. Order rejected.");
            return;
        }
        if (!readLine(" Confirm order (y/n): ").equalsIgnoreCase("y")) {
            System.out.println(" Order cancelled.");
            return;
        }
        if (user.buy(stock, qty)) {
            System.out.println(" BUY successful. New cash balance: " + money(user.getBalance()));
            history.record(market.getTick(), user, market);
        } else {
            System.out.println(" Order failed.");
        }
    }

    private static void sellStock(Market market, User user, PortfolioHistory history) {
        System.out.println("\n--- SELL STOCK ---");
        if (user.getPortfolio().isEmpty()) {
            System.out.println(" You do not hold any stocks yet.");
            return;
        }
        Stock stock = askStock(market);
        if (stock == null) return;

        int held = user.getQuantity(stock.getSymbol());
        if (held == 0) {
            System.out.println(" You do not own any shares of " + stock.getSymbol() + ".");
            return;
        }
        System.out.println(" You hold " + held + " share(s) of " + stock.getSymbol()
                + " at average cost " + money(user.getHolding(stock.getSymbol()).getAveragePrice()));

        int qty = readInt(" Quantity to sell: ");
        if (qty <= 0 || qty > held) {
            System.out.println(" Invalid quantity.");
            return;
        }
        double proceeds = Stock.round2(stock.getPrice() * qty);
        Holding h = user.getHolding(stock.getSymbol());
        double realised = Stock.round2(proceeds - h.getAveragePrice() * qty);
        System.out.println(" Sale value: " + money(proceeds)
                + "   Realised P/L: " + money(realised));

        if (!readLine(" Confirm order (y/n): ").equalsIgnoreCase("y")) {
            System.out.println(" Order cancelled.");
            return;
        }
        if (user.sell(stock, qty)) {
            System.out.println(" SELL successful. New cash balance: " + money(user.getBalance()));
            history.record(market.getTick(), user, market);
        } else {
            System.out.println(" Order failed.");
        }
    }

    // ---------------------------------------------------------------
    //  Reporting
    // ---------------------------------------------------------------
    private static void viewPortfolio(Market market, User user) {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("                        MY PORTFOLIO");
        System.out.println("-------------------------------------------------------------------------");
        if (user.getPortfolio().isEmpty()) {
            System.out.println(" No holdings. Cash balance: " + money(user.getBalance()));
            System.out.println("-------------------------------------------------------------------------");
            return;
        }
        System.out.printf("%-8s %8s %10s %10s %12s %12s %10s%n",
                "SYMBOL", "QTY", "AVG COST", "LTP", "INVESTED", "VALUE", "P/L");
        System.out.println("-------------------------------------------------------------------------");
        for (Holding h : user.getPortfolio().values()) {
            Stock s = market.getStock(h.getSymbol());
            double ltp = s == null ? 0 : s.getPrice();
            System.out.printf("%-8s %8d %10.2f %10.2f %12.2f %12.2f %10.2f%n",
                    h.getSymbol(), h.getQuantity(), h.getAveragePrice(), ltp,
                    h.getInvestedValue(), h.getMarketValue(ltp), h.getProfitLoss(ltp));
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("%-8s %8s %10s %10s %12.2f %12.2f %10.2f%n", "TOTAL", "", "", "",
                user.getTotalInvested(), user.getPortfolioValue(market), user.getTotalProfitLoss(market));
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" Cash Balance   : " + money(user.getBalance()));
        System.out.println(" Portfolio Value: " + money(user.getPortfolioValue(market)));
        System.out.println(" Net Worth      : " + money(user.getNetWorth(market)));
        System.out.println("-------------------------------------------------------------------------");
    }

    private static void viewTransactions(User user) {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("                     TRANSACTION HISTORY");
        System.out.println("-------------------------------------------------------------------------");
        List<Transaction> list = user.getTransactions();
        if (list.isEmpty()) {
            System.out.println(" No transactions yet.");
            System.out.println("-------------------------------------------------------------------------");
            return;
        }
        System.out.printf("%-19s %-5s %-8s %6s %12s %12s%n",
                "DATE & TIME", "TYPE", "SYMBOL", "QTY", "PRICE", "AMOUNT");
        System.out.println("-------------------------------------------------------------------------");
        for (Transaction t : list) {
            System.out.println(" " + t);
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" Total transactions: " + list.size());
        System.out.println("-------------------------------------------------------------------------");
    }

    private static void depositCash(User user) {
        System.out.println("\n--- ADD FUNDS ---");
        System.out.println(" Current cash balance: " + money(user.getBalance()));
        String v = readLine(" Amount to add: ");
        try {
            double amt = Double.parseDouble(v);
            if (amt <= 0) {
                System.out.println(" Amount must be positive.");
                return;
            }
            user.deposit(amt);
            System.out.println(" Funds added. New cash balance: " + money(user.getBalance()));
        } catch (NumberFormatException e) {
            System.out.println(" Invalid amount.");
        }
    }

    // ---------------------------------------------------------------
    //  Helpers
    // ---------------------------------------------------------------
    private static Stock askStock(Market market) {
        String symbol = readLine(" Enter stock symbol: ");
        Stock s = market.getStock(symbol);
        if (s == null) {
            System.out.println(" Unknown symbol. Please check the market table.");
        }
        return s;
    }

    private static String money(double v) {
        return String.format("%,.2f", v);
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int readInt(String prompt) {
        while (true) {
            String v = readLine(prompt);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                System.out.print(" Please enter a valid number: ");
            }
        }
    }
}
