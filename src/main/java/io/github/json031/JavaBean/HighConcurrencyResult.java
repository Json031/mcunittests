package io.github.json031.JavaBean;

/**
 * High Concurrency Result.
 */
public class HighConcurrencyResult {
    /**
     * total request times.
     */
    public final int total;
    /**
     * request success times.
     */
    public final int success;
    /**
     * request failed times.
     */
    public final int failed;
    /**
     * request average cost timeMillis.
     */
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