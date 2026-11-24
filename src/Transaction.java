import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A single buy or sell transaction performed by a user.
 */
public class Transaction {

    public enum Type { BUY, SELL }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final LocalDateTime time;

    public Transaction(Type type, String symbol, int quantity, double price, LocalDateTime time) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.time = time;
    }

    public Transaction(Type type, String symbol, int quantity, double price) {
        this(type, symbol, quantity, price, LocalDateTime.now());
    }

    public Type getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return Stock.round2(quantity * price);
    }

    public LocalDateTime getTime() {
        return time;
    }

    /** CSV form: type,symbol,quantity,price,timestamp */
    public String toCsv() {
        return type + "," + symbol + "," + quantity + "," + price + "," + time.format(FMT);
    }

    public static Transaction fromCsv(String line) {
        String[] p = line.split(",");
        if (p.length < 5) return null;
        return new Transaction(Type.valueOf(p[0]), p[1], Integer.parseInt(p[2]),
                Double.parseDouble(p[3]), LocalDateTime.parse(p[4], FMT));
    }

    @Override
    public String toString() {
        return String.format("%-19s %-5s %-8s %6d @ %9.2f = %11.2f",
                time.format(FMT), type, symbol, quantity, price, getAmount());
    }
}
