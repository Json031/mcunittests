package io.github.json031.apitests;

import io.github.json031.JavaBean.HighConcurrencyResult;
import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.unittests.RequestUnitTests;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * This class is used for unit testing api High Concurrency.
 */
public class MCHighConcurrencyTests {

    public MCApiTests mcApiTests = new MCApiTests();

    /**
     * 运行并发测试
     * @param url        请求地址
     * @param threadCount 并发线程数
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param timeoutSeconds 最大允许超时时间（秒）
     * @param verbose       是否打印响应
     * @return 统计结果
     */
    public HighConcurrencyResult highConcurrencyTestWithTimeoutSeconds(String url,
                                                                       int threadCount,
                                                                       HttpMethod method,
                                                                       Map<String, Object> params,
                                                                       Map<String, String> headers,
                                                                       long timeoutSeconds,
                                                                       boolean verbose) {
        return this.highConcurrencyTestWithTimeoutMillis(url, threadCount, method, params, headers, timeoutSeconds * 1000, verbose);
    }

    /**
     * 运行并发测试
     * @param url        请求地址
     * @param threadCount 并发线程数
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param timeoutMillis 最大允许超时时间（毫秒）
     * @param verbose       是否打印响应
     * @return 统计结果
     */
    public HighConcurrencyResult highConcurrencyTestWithTimeoutMillis(String url,
                                                                      int threadCount,
                                                                      HttpMethod method,
                                                                      Map<String, Object> params,
                                                                      Map<String, String> headers,
                                                                      long timeoutMillis,
                                                                      boolean verbose) {
        //create an executor to manager api request thread
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        //the api request thread results
        List<Future<Long>> futures = new ArrayList<>();
        //thread safe for logging success count
        AtomicInteger successCount = new AtomicInteger();
        //thread safe for logging fail count
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                long start = System.nanoTime();
                try {
                    boolean withinTimeoutMillis = this.mcApiTests.assertApiRespondsWithinTimeoutMillis(url, method, params, headers, timeoutMillis, verbose); // 执行请求
                    if (!withinTimeoutMillis) {
                        failCount.incrementAndGet();
                    } else {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
                return (System.nanoTime() - start) / 1_000_000;
            }));
        }

        executor.shutdown();
        long totalTime = 0;
        int completed = 0;
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
            for (Future<Long> future : futures) {
                long time = future.get();
                totalTime += time;
                completed++;
            }

        } catch (Exception ignore) {
        }

        long avg = completed > 0 ? totalTime / completed : 0;
        return new HighConcurrencyResult(threadCount, successCount.get(), failCount.get(), avg);
    }

    /**
     * 运行详细的并发测试，返回每个请求的详细信息
     *
     * @param url        请求地址
     * @param threadCount 并发线程数
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param verbose       是否打印响应
     * @return 详细的并发测试结果
     */
    public DetailedConcurrencyResult detailedConcurrencyTest(String url,
                                                             int threadCount,
                                                             HttpMethod method,
                                                             Map<String, Object> params,
                                                             Map<String, String> headers,
                                                             boolean verbose) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<RequestUnitTestsResult>> futures = new ArrayList<>();
        Instant testStartTime = Instant.now();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() ->
                    RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose)
            ));
        }

        executor.shutdown();
        List<RequestUnitTestsResult> results = new ArrayList<>();

        try {
            executor.awaitTermination(120, TimeUnit.SECONDS);
            for (Future<RequestUnitTestsResult> future : futures) {
                try {
                    RequestUnitTestsResult result = future.get();
                    if (result != null) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    // 记录异常但继续处理其他结果
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Instant testEndTime = Instant.now();
        return new DetailedConcurrencyResult(results, testStartTime, testEndTime, threadCount);
    }

    /**
     * 压力测试 - 逐步增加并发数，找到系统的性能拐点
     *
     * @param url              请求地址
     * @param startThreads     起始线程数
     * @param maxThreads       最大线程数
     * @param stepSize         每次增加的线程数
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param acceptableFailRate 可接受的失败率（0.0-1.0）
     * @param verbose          是否打印响应
     * @return 压力测试结果列表
     */
    public List<HighConcurrencyResult> stressTest(String url,
                                                  int startThreads,
                                                  int maxThreads,
                                                  int stepSize,
                                                  HttpMethod method,
                                                  Map<String, Object> params,
                                                  Map<String, String> headers,
                                                  double acceptableFailRate,
                                                  long timeoutMillis,
                                                  boolean verbose) {
        List<HighConcurrencyResult> results = new ArrayList<>();

        for (int threadCount = startThreads; threadCount <= maxThreads; threadCount += stepSize) {
            if (verbose) {
                System.out.println("Testing with " + threadCount + " concurrent threads...");
            }

            HighConcurrencyResult result = highConcurrencyTestWithTimeoutMillis(
                    url, threadCount, method, params, headers, timeoutMillis, false
            );
            results.add(result);

            // 如果失败率超过阈值，提前终止测试
            double failRate = (double) result.failed / result.total;
            if (failRate > acceptableFailRate) {
                if (verbose) {
                    System.out.println("Fail rate " + String.format("%.2f%%", failRate * 100) +
                            " exceeds acceptable rate " + String.format("%.2f%%", acceptableFailRate * 100));
                }
                break;
            }

            // 短暂休息，避免连续压力
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return results;
    }

    /**
     * 持续负载测试 - 在指定时间内持续发送请求
     *
     * @param url              请求地址
     * @param durationSeconds  测试持续时间（秒）
     * @param requestsPerSecond 每秒请求数（QPS）
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param verbose          是否打印响应
     * @return 持续负载测试结果
     */
    public SustainedLoadResult sustainedLoadTest(String url,
                                                 int durationSeconds,
                                                 int requestsPerSecond,
                                                 HttpMethod method,
                                                 Map<String, Object> params,
                                                 Map<String, String> headers,
                                                 boolean verbose) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(requestsPerSecond);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        Instant startTime = Instant.now();
        long intervalMillis = 1000 / requestsPerSecond;

        // 调度任务
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            long reqStart = System.currentTimeMillis();
            try {
                RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(
                        url, method, params, headers, false
                );

                long responseTime = System.currentTimeMillis() - reqStart;
                responseTimes.add(responseTime);
                totalResponseTime.addAndGet(responseTime);

                if (result != null && result.isSuccess) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        }, 0, intervalMillis, TimeUnit.MILLISECONDS);

        // 等待测试完成
        try {
            Thread.sleep(durationSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        task.cancel(false);
        scheduler.shutdown();

        try {
            scheduler.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Instant endTime = Instant.now();

        return new SustainedLoadResult(
                successCount.get(),
                failCount.get(),
                responseTimes,
                startTime,
                endTime,
                requestsPerSecond
        );
    }

    /**
     * 峰值测试 - 模拟流量突然激增的场景
     *
     * @param url              请求地址
     * @param normalThreads    正常负载的线程数
     * @param peakThreads      峰值负载的线程数
     * @param normalDuration   正常负载持续时间（秒）
     * @param peakDuration     峰值负载持续时间（秒）
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param verbose          是否打印响应
     * @return 峰值测试结果
     */
    public PeakLoadResult peakLoadTest(String url,
                                       int normalThreads,
                                       int peakThreads,
                                       int normalDuration,
                                       int peakDuration,
                                       HttpMethod method,
                                       Map<String, Object> params,
                                       Map<String, String> headers,
                                       long timeoutMillis,
                                       boolean verbose) {
        if (verbose) {
            System.out.println("Starting normal load phase with " + normalThreads + " threads for " + normalDuration + "s...");
        }

        // 正常负载阶段
        HighConcurrencyResult normalResult = highConcurrencyTestWithTimeoutMillis(
                url, normalThreads, method, params, headers, timeoutMillis, false
        );

        try {
            Thread.sleep(normalDuration * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (verbose) {
            System.out.println("Starting peak load phase with " + peakThreads + " threads for " + peakDuration + "s...");
        }

        // 峰值负载阶段
        HighConcurrencyResult peakResult = highConcurrencyTestWithTimeoutMillis(
                url, peakThreads, method, params, headers, timeoutMillis, false
        );

        try {
            Thread.sleep(peakDuration * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (verbose) {
            System.out.println("Returning to normal load phase...");
        }

        // 恢复正常负载
        HighConcurrencyResult recoveryResult = highConcurrencyTestWithTimeoutMillis(
                url, normalThreads, method, params, headers, timeoutMillis, false
        );

        return new PeakLoadResult(normalResult, peakResult, recoveryResult);
    }

    /**
     * 稳定性测试 - 长时间运行，检测内存泄漏或性能退化
     *
     * @param url              请求地址
     * @param threadCount      并发线程数
     * @param iterations       迭代次数
     * @param intervalSeconds  每次迭代间隔（秒）
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param verbose          是否打印响应
     * @return 稳定性测试结果列表
     */
    public List<HighConcurrencyResult> stabilityTest(String url,
                                                     int threadCount,
                                                     int iterations,
                                                     int intervalSeconds,
                                                     HttpMethod method,
                                                     Map<String, Object> params,
                                                     Map<String, String> headers,
                                                     long timeoutMillis,
                                                     boolean verbose) {
        List<HighConcurrencyResult> results = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            if (verbose) {
                System.out.println("Stability test iteration " + (i + 1) + "/" + iterations);
            }

            HighConcurrencyResult result = highConcurrencyTestWithTimeoutMillis(
                    url, threadCount, method, params, headers, timeoutMillis, false
            );
            results.add(result);

            if (i < iterations - 1) {
                try {
                    Thread.sleep(intervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return results;
    }

    /**
     * 容量测试 - 找到系统的最大吞吐量
     *
     * @param url              请求地址
     * @param initialThreads   初始线程数
     * @param maxThreads       最大线程数
     * @param increment        每次增加的线程数
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param throughputThreshold 吞吐量阈值（请求/秒），低于此值认为系统过载
     * @param verbose          是否打印响应
     * @return 容量测试结果
     */
    public CapacityTestResult capacityTest(String url,
                                           int initialThreads,
                                           int maxThreads,
                                           int increment,
                                           HttpMethod method,
                                           Map<String, Object> params,
                                           Map<String, String> headers,
                                           double throughputThreshold,
                                           long timeoutMillis,
                                           boolean verbose) {
        List<CapacityDataPoint> dataPoints = new ArrayList<>();
        int optimalThreadCount = initialThreads;
        double maxThroughput = 0;

        for (int threads = initialThreads; threads <= maxThreads; threads += increment) {
            if (verbose) {
                System.out.println("Capacity test with " + threads + " threads...");
            }

            Instant start = Instant.now();
            HighConcurrencyResult result = highConcurrencyTestWithTimeoutMillis(
                    url, threads, method, params, headers, timeoutMillis, false
            );
            Instant end = Instant.now();

            double durationSeconds = Duration.between(start, end).toMillis() / 1000.0;
            double throughput = result.success / durationSeconds;

            CapacityDataPoint dataPoint = new CapacityDataPoint(
                    threads,
                    result.success,
                    result.failed,
                    result.avgResponseTimeMillis,
                    throughput
            );
            dataPoints.add(dataPoint);

            if (throughput > maxThroughput) {
                maxThroughput = throughput;
                optimalThreadCount = threads;
            }

            // 如果吞吐量开始下降，说明系统过载
            if (throughput < throughputThreshold) {
                if (verbose) {
                    System.out.println("Throughput dropped below threshold, stopping test.");
                }
                break;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return new CapacityTestResult(dataPoints, optimalThreadCount, maxThroughput);
    }

    /**
     * 并发安全性测试 - 验证并发访问下的数据一致性
     *
     * @param url              请求地址
     * @param threadCount      并发线程数
     * @param iterations       每个线程的迭代次数
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param consistencyChecker 一致性检查函数
     * @param verbose          是否打印响应
     * @return 是否通过一致性检查
     */
    public boolean concurrencySafetyTest(String url,
                                         int threadCount,
                                         int iterations,
                                         HttpMethod method,
                                         Map<String, Object> params,
                                         Map<String, String> headers,
                                         ConsistencyChecker consistencyChecker,
                                         boolean verbose) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<List<String>>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                List<String> responses = new ArrayList<>();
                for (int j = 0; j < iterations; j++) {
                    RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(
                            url, method, params, headers, false
                    );
                    if (result != null && result.response != null && result.response.getBody() != null) {
                        responses.add(result.response.getBody());
                    }
                }
                return responses;
            }));
        }

        executor.shutdown();
        List<String> allResponses = new ArrayList<>();

        try {
            executor.awaitTermination(120, TimeUnit.SECONDS);
            for (Future<List<String>> future : futures) {
                allResponses.addAll(future.get());
            }
        } catch (Exception e) {
            if (verbose) {
                System.out.println("Error during concurrency safety test: " + e.getMessage());
            }
            return false;
        }

        return consistencyChecker.check(allResponses);
    }

    /**
     * 一致性检查器接口
     */
    @FunctionalInterface
    public interface ConsistencyChecker {
        boolean check(List<String> responses);
    }

    /**
     * 详细并发测试结果
     */
    public static class DetailedConcurrencyResult {
        public final List<RequestUnitTestsResult> results;
        public final Instant testStartTime;
        public final Instant testEndTime;
        public final int threadCount;
        public final int successCount;
        public final int failCount;
        public final long minResponseTime;
        public final long maxResponseTime;
        public final long avgResponseTime;
        public final long medianResponseTime;
        public final long p95ResponseTime;
        public final long p99ResponseTime;

        public DetailedConcurrencyResult(List<RequestUnitTestsResult> results,
                                         Instant testStartTime,
                                         Instant testEndTime,
                                         int threadCount) {
            this.results = results;
            this.testStartTime = testStartTime;
            this.testEndTime = testEndTime;
            this.threadCount = threadCount;

            this.successCount = (int) results.stream().filter(r -> r.isSuccess).count();
            this.failCount = results.size() - successCount;

            List<Long> responseTimes = results.stream()
                    .map(r -> r.durationMillis)
                    .sorted()
                    .collect(Collectors.toList());

            this.minResponseTime = responseTimes.isEmpty() ? 0 : responseTimes.get(0);
            this.maxResponseTime = responseTimes.isEmpty() ? 0 : responseTimes.get(responseTimes.size() - 1);
            this.avgResponseTime = responseTimes.isEmpty() ? 0 :
                    (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            this.medianResponseTime = responseTimes.isEmpty() ? 0 :
                    responseTimes.get(responseTimes.size() / 2);
            this.p95ResponseTime = responseTimes.isEmpty() ? 0 :
                    responseTimes.get((int) (responseTimes.size() * 0.95));
            this.p99ResponseTime = responseTimes.isEmpty() ? 0 :
                    responseTimes.get((int) (responseTimes.size() * 0.99));
        }

        @Override
        public String toString() {
            return String.format(
                    "DetailedConcurrencyResult{threads=%d, success=%d, fail=%d, " +
                            "min=%dms, max=%dms, avg=%dms, median=%dms, p95=%dms, p99=%dms}",
                    threadCount, successCount, failCount,
                    minResponseTime, maxResponseTime, avgResponseTime,
                    medianResponseTime, p95ResponseTime, p99ResponseTime
            );
        }
    }

    /**
     * 持续负载测试结果
     */
    public static class SustainedLoadResult {
        public final int successCount;
        public final int failCount;
        public final List<Long> responseTimes;
        public final Instant startTime;
        public final Instant endTime;
        public final int targetQPS;
        public final double actualQPS;
        public final long avgResponseTime;
        public final double successRate;

        public SustainedLoadResult(int successCount,
                                   int failCount,
                                   List<Long> responseTimes,
                                   Instant startTime,
                                   Instant endTime,
                                   int targetQPS) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.responseTimes = responseTimes;
            this.startTime = startTime;
            this.endTime = endTime;
            this.targetQPS = targetQPS;

            long durationSeconds = Duration.between(startTime, endTime).getSeconds();
            this.actualQPS = durationSeconds > 0 ? (double) (successCount + failCount) / durationSeconds : 0;
            this.avgResponseTime = responseTimes.isEmpty() ? 0 :
                    (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            this.successRate = (successCount + failCount) > 0 ?
                    (double) successCount / (successCount + failCount) : 0;
        }

        @Override
        public String toString() {
            return String.format(
                    "SustainedLoadResult{success=%d, fail=%d, targetQPS=%d, actualQPS=%.2f, " +
                            "avgResponse=%dms, successRate=%.2f%%}",
                    successCount, failCount, targetQPS, actualQPS, avgResponseTime, successRate * 100
            );
        }
    }

    /**
     * 峰值负载测试结果
     */
    public static class PeakLoadResult {
        public final HighConcurrencyResult normalLoad;
        public final HighConcurrencyResult peakLoad;
        public final HighConcurrencyResult recoveryLoad;

        public PeakLoadResult(HighConcurrencyResult normalLoad,
                              HighConcurrencyResult peakLoad,
                              HighConcurrencyResult recoveryLoad) {
            this.normalLoad = normalLoad;
            this.peakLoad = peakLoad;
            this.recoveryLoad = recoveryLoad;
        }

        public boolean isSystemResilient() {
            // 系统在峰值后能否恢复到正常水平
            double normalSuccessRate = (double) normalLoad.success / normalLoad.total;
            double recoverySuccessRate = (double) recoveryLoad.success / recoveryLoad.total;
            return Math.abs(normalSuccessRate - recoverySuccessRate) < 0.1; // 10% 容差
        }

        @Override
        public String toString() {
            return String.format(
                    "PeakLoadResult{normal=%s, peak=%s, recovery=%s, resilient=%s}",
                    normalLoad, peakLoad, recoveryLoad, isSystemResilient()
            );
        }
    }

    /**
     * 容量测试数据点
     */
    public static class CapacityDataPoint {
        public final int threadCount;
        public final int successCount;
        public final int failCount;
        public final long avgResponseTime;
        public final double throughput;

        public CapacityDataPoint(int threadCount,
                                 int successCount,
                                 int failCount,
                                 long avgResponseTime,
                                 double throughput) {
            this.threadCount = threadCount;
            this.successCount = successCount;
            this.failCount = failCount;
            this.avgResponseTime = avgResponseTime;
            this.throughput = throughput;
        }

        @Override
        public String toString() {
            return String.format(
                    "CapacityDataPoint{threads=%d, success=%d, fail=%d, avgTime=%dms, throughput=%.2f req/s}",
                    threadCount, successCount, failCount, avgResponseTime, throughput
            );
        }
    }

    /**
     * 容量测试结果
     */
    public static class CapacityTestResult {
        public final List<CapacityDataPoint> dataPoints;
        public final int optimalThreadCount;
        public final double maxThroughput;

        public CapacityTestResult(List<CapacityDataPoint> dataPoints,
                                  int optimalThreadCount,
                                  double maxThroughput) {
            this.dataPoints = dataPoints;
            this.optimalThreadCount = optimalThreadCount;
            this.maxThroughput = maxThroughput;
        }

        @Override
        public String toString() {
            return String.format(
                    "CapacityTestResult{optimalThreads=%d, maxThroughput=%.2f req/s, dataPoints=%d}",
                    optimalThreadCount, maxThroughput, dataPoints.size()
            );
        }
    }
}