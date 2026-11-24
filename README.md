# CodeAlpha_StockTradingPlatform

**Java Programming Internship — Task 2: Stock Trading Platform**
CodeAlpha | Student: Piyush Vishnupant Divase | ID: CA/DF1/433397

---

## Task Requirements Covered

| Requirement | Implementation |
|---|---|
| Simulate a basic stock trading environment | `Market` class simulates 8 stocks with random price movement per tick |
| Market data display and buy/sell operations | Menu option 1 (market watch table) + options 2 & 3 with cash/quantity validation |
| Allow users to track portfolio performance over time | `PortfolioHistory` records a net-worth snapshots per tick/trade and prints a chart |
| Use OOP to manage stocks, users and transactions | `Stock`, `User`, `Holding`, `Transaction`, `Market`, `PortfolioManager` |
| Optionally include file I/O or database to persist portfolio data | CSV file I/O in `PortfolioManager` (account, holdings, transactions, performance) |

## Project Structure

```
CodeAlpha_StockTradingPlatform/
├── src/
│   ├── Stock.java               # Stock model: price, change %, trend
│   ├── Holding.java             # Position: qty + weighted average cost
│   ├── Transaction.java         # Buy/Sell record with timestamp
│   ├── User.java                # Cash balance, holdings, trade operations
│   ├── Market.java              # Simulated market + price tick engine
│   ├── Snapshot.java            # One point of the performance curve
│   ├── PortfolioHistory.java    # Performance tracking + text chart
│   ├── PortfolioManager.java    # File I/O persistence (CSV)
│   └── StockTradingPlatform.java# Main class + console menu (entry point)
├── data/                        # Auto-created when the app saves:
│   ├── account.csv              #   username, balance
│   ├── holdings.csv             #   symbol, quantity, total cost
│   ├── transactions.csv         #   full trade history
│   └── performance.csv          #   net worth per tick
├── run.sh
└── README.md
```

## How to Compile and Run

```bash
javac -d out src/*.java
java -cp out StockTradingPlatform
```

or:

```bash
chmod +x run.sh
./run.sh
```

> If your JDK reports an encoding error on Windows, compile with
> `javac -encoding UTF-8 -d out src/*.java`.

## Menu Options

```
1. View Market Data                    -> live market watch table
2. Buy Stock                           -> validated against available cash
3. Sell Stock                          -> validated against held quantity
4. View Portfolio                      -> qty, avg cost, LTP, invested, value, P/L
5. Transaction History                 -> every buy/sell with date & time
6. Portfolio Performance Chart         -> net worth over time + peak/lowest
7. Next Market Tick                    -> simulate price movement (-3% .. +3%)
8. Add Funds                           -> deposit cash
9. Save & Exit                         -> writes all CSV files under data/
```

## Sample Output

```
-------------------------------------------------------------------------
                     MARKET WATCH  (tick #2)
-------------------------------------------------------------------------
SYMBOL   COMPANY                       PRICE    CHANGE   CHANGE% TREND
-------------------------------------------------------------------------
 TCS      Tata Consultancy Services    3844.63    -5.37     -0.14%   v DOWN
 INFY     Infosys Ltd                  1653.98    33.98      2.10%   ^ UP
 ...
-------------------------------------------------------------------------

-------------------------------------------------------------------------
                        MY PORTFOLIO
-------------------------------------------------------------------------
SYMBOL        QTY   AVG COST        LTP     INVESTED        VALUE        P/L
-------------------------------------------------------------------------
TCS            10    3850.00    3844.63     38500.00     38446.30     -53.70
INFY           20    1620.00    1653.98     32400.00     33079.60     679.60
-------------------------------------------------------------------------
TOTAL                                       70900.00     71525.90     625.90
-------------------------------------------------------------------------
 Cash Balance   : 29,100.00
 Portfolio Value: 71,525.90
 Net Worth      : 100,625.90
-------------------------------------------------------------------------
```

## Design Notes

- **Encapsulation** — prices can only change through `Stock.updatePrice()`; cash only
  through `User.buy()` / `sell()` / `deposit()` so balances can never go negative.
- **Weighted average cost** in `Holding` gives a correct realised P/L when part of a
  position is sold.
- **Persistence format** is plain CSV so it can be opened in Excel or migrated to a
  database later; the app reloads the account, holdings and full trade history on start.
- **Performance tracking** is snapshot based: net worth (cash + holdings) is recorded
  on every market tick and after every executed trade.
