import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Records the net worth of the portfolio over time (one snapshot per market tick
 * and after every trade) and prints a simple performance chart.
 */
public class PortfolioHistory {

    private final List<Snapshot> snapshots = new ArrayList<>();

    public void record(int tick, User user, Market market) {
        snapshots.add(new Snapshot(tick, user.getNetWorth(market), user.getPortfolioValue(market)));
    }

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    public double getStartingNetWorth() {
        return snapshots.isEmpty() ? 0.0 : snapshots.get(0).getNetWorth();
    }

    public double getLatestNetWorth() {
        return snapshots.isEmpty() ? 0.0 : snapshots.get(snapshots.size() - 1).getNetWorth();
    }

    public double getOverallReturn() {
        return Stock.round2(getLatestNetWorth() - getStartingNetWorth());
    }

    public double getOverallReturnPercent() {
        double start = getStartingNetWorth();
        if (start == 0) return 0.0;
        return Stock.round2((getOverallReturn() / start) * 100.0);
    }

    public Snapshot getPeak() {
        return snapshots.stream().max((a, b) -> Double.compare(a.getNetWorth(), b.getNetWorth())).orElse(null);
    }

    public Snapshot getTrough() {
        return snapshots.stream().min((a, b) -> Double.compare(a.getNetWorth(), b.getNetWorth())).orElse(null);
    }

    /** Text based performance chart of the recorded net worth. */
    public String buildChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n-------------------------------------------------------------------------\n");
        sb.append("                  PORTFOLIO PERFORMANCE OVER TIME\n");
        sb.append("-------------------------------------------------------------------------\n");
        if (snapshots.isEmpty()) {
            sb.append(" No performance data recorded yet.\n");
            return sb.toString();
        }

        double min = snapshots.stream().mapToDouble(Snapshot::getNetWorth).min().orElse(0);
        double max = snapshots.stream().mapToDouble(Snapshot::getNetWorth).max().orElse(0);
        if (max - min < 1) {
            max = min + 1;
        }

        List<Snapshot> view = snapshots;
        int step = Math.max(1, view.size() / 25);          // keep the chart readable
        int barWidth = 30;
        for (int i = 0; i < view.size(); i += step) {
            Snapshot s = view.get(i);
            int bars = (int) Math.round(((s.getNetWorth() - min) / (max - min)) * barWidth);
            sb.append(String.format(" tick %-4d %10.2f |%s%n",
                    s.getTick(), s.getNetWorth(), "#".repeat(Math.max(0, bars))));
        }

        sb.append("-------------------------------------------------------------------------\n");
        sb.append(String.format(" Start Net Worth : %10.2f%n", getStartingNetWorth()));
        sb.append(String.format(" Latest Net Worth: %10.2f%n", getLatestNetWorth()));
        sb.append(String.format(" Overall Return  : %10.2f  (%.2f%%)%n", getOverallReturn(), getOverallReturnPercent()));
        if (getPeak() != null) {
            sb.append(String.format(" Peak Net Worth  : %10.2f  at tick %d%n", getPeak().getNetWorth(), getPeak().getTick()));
            sb.append(String.format(" Lowest Net Worth: %10.2f  at tick %d%n", getTrough().getNetWorth(), getTrough().getTick()));
        }
        sb.append("-------------------------------------------------------------------------\n");
        return sb.toString();
    }

    public void save(String file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(file))) {
            w.write("tick,netWorth,portfolioValue");
            w.newLine();
            for (Snapshot s : snapshots) {
                w.write(s.getTick() + "," + s.getNetWorth() + "," + s.getPortfolioValue());
                w.newLine();
            }
        }
    }
}
