package io.github.json031.JavaBean;

public class HighConcurrencyResult {
    public final int total;
    public final int success;
    public final int failed;
    public final long avgResponseTimeMillis;

    public HighConcurrencyResult(int total, int success, int failed, long avgResponseTimeMillis) {
        this.total = total;
        this.success = success;
        this.failed = failed;
        this.avgResponseTimeMillis = avgResponseTimeMillis;
    }

    @Override
    public String toString() {
        return "=== Load Test Result ===\n" +
                "Total Requests: " + this.total + "\n" +
                "Successful:     " + this.success + "\n" +
                "Failed:         " + this.failed + "\n" +
                "Avg Time (ms):  " + this.avgResponseTimeMillis;
    }
}