import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file I/O so that the user's account, holdings, transaction history
 * and portfolio performance history survive between runs.
 *
 * File layout (all plain CSV):
 *   data/account.csv        -> username,balance
 *   data/holdings.csv       -> symbol,quantity,totalCost
 *   data/transactions.csv   -> type,symbol,quantity,price,timestamp
 *   data/performance.csv    -> tick,netWorth,portfolioValue,marketTick
 */
public class PortfolioManager {

    private static final String ACCOUNT  = "data/account.csv";
    private static final String HOLDINGS = "data/holdings.csv";
    private static final String TXNS     = "data/transactions.csv";
    private static final String PERF     = "data/performance.csv";

    /** Loads a user from disk, or creates a fresh one with the given starting cash. */
    public User loadOrCreate(double startingCash) {
        User user = new User("investor", startingCash);
        try {
            Path acc = Paths.get(ACCOUNT);
            if (Files.exists(acc)) {
                List<String> lines = Files.readAllLines(acc);
                if (lines.size() >= 2) {
                    String[] p = lines.get(1).split(",");
                    user = new User(p[0], Double.parseDouble(p[1]));
                }
            }
            Path hold = Paths.get(HOLDINGS);
            if (Files.exists(hold)) {
                for (String line : Files.readAllLines(hold)) {
                    if (line.isBlank() || line.startsWith("symbol")) continue;
                    String[] p = line.split(",");
                    if (p.length < 3) continue;
                    Holding h = new Holding(p[0]);
                    h.add(Integer.parseInt(p[1]), 0.0);
                    h.forceCost(Double.parseDouble(p[2]));
                    user.getPortfolio().put(p[0], h);
                }
            }
            Path t = Paths.get(TXNS);
            if (Files.exists(t)) {
                for (String line : Files.readAllLines(t)) {
                    if (line.isBlank() || line.startsWith("type")) continue;
                    Transaction tx = Transaction.fromCsv(line);
                    if (tx != null) user.getTransactions().add(tx);
                }
            }
        } catch (Exception e) {
            System.out.println(" Warning: could not load saved data (" + e.getMessage() + ")");
        }
        return user;
    }

    public void save(User user, PortfolioHistory history) {
        try {
            Files.createDirectories(Paths.get("data"));

            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(ACCOUNT))) {
                w.write("username,balance");
                w.newLine();
                w.write(user.getUsername() + "," + user.getBalance());
                w.newLine();
            }

            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(HOLDINGS))) {
                w.write("symbol,quantity,totalCost");
                w.newLine();
                for (Holding h : user.getPortfolio().values()) {
                    w.write(h.getSymbol() + "," + h.getQuantity() + "," + h.getInvestedValue());
                    w.newLine();
                }
            }

            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(TXNS))) {
                w.write("type,symbol,quantity,price,timestamp");
                w.newLine();
                for (Transaction tx : user.getTransactions()) {
                    w.write(tx.toCsv());
                    w.newLine();
                }
            }

            history.save(PERF);
        } catch (IOException e) {
            System.out.println(" Error while saving data: " + e.getMessage());
        }
    }

    /** Reads the saved performance history back for reporting. */
    public static List<Snapshot> loadPerformance() {
        List<Snapshot> list = new ArrayList<>();
        Path p = Paths.get(PERF);
        if (!Files.exists(p)) return list;
        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = line.split(",");
                if (c.length < 3) continue;
                list.add(new Snapshot(Integer.parseInt(c[0]), Double.parseDouble(c[1]),
                        Double.parseDouble(c[2])));
            }
        } catch (Exception ignored) {
        }
        return list;
    }
}
