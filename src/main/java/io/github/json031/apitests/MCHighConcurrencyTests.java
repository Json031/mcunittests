package io.github.json031.apitests;

import io.github.json031.JavaBean.HighConcurrencyResult;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used for unit testing api High Concurrency.
 */
public class MCHighConcurrencyTests {

    private final MCApiTests mcApiTests = new MCApiTests();

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
        return this.highConcurrencyTestWithTimeoutMillis(url, threadCount, method, params, headers, timeoutSeconds, verbose);
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
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Long>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                long start = System.nanoTime();
                try {
                    long rsptime = this.mcApiTests.assertApiRespondsWithinTimeoutMillis(url, method, params, headers, timeoutMillis, verbose); // 执行请求
                    System.out.println("API Response time: " + rsptime);
                    if (rsptime >= timeoutMillis) {
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
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assertions.fail("High concurrency test failed for url: " + url);
        }

        long totalTime = 0;
        int completed = 0;
        for (Future<Long> future : futures) {
            try {
                long time = future.get();
                totalTime += time;
                completed++;
            } catch (Exception ignored) {
            }
        }

        long avg = completed > 0 ? totalTime / completed : -1;
        return new HighConcurrencyResult(threadCount, successCount.get(), failCount.get(), avg);
    }
}
