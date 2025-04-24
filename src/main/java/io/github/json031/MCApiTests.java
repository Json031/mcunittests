package io.github.json031;

import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public class MCApiTests {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 通用测试方法，验证给定 API 是否能在 timeout 秒内响应
     *
     * @param url     完整的 API 地址（包括 http/https）
     * @param timeout 最大允许超时时间（秒）
     * @return durationMillis 响应时间（毫秒）
     */
    public long assertApiRespondsWithinTimeout(String url, long timeout, boolean verbose) {
        return this.assertApiRespondsWithinTimeoutMillis(url, timeout * 1000, verbose);
    }

    /**
     * 通用测试方法，验证给定 API 是否能在 timeout 秒内响应
     *
     * @param url     完整的 API 地址（包括 http/https）
     * @param timeoutMillis 最大允许超时时间（毫秒）
     * @return durationMillis 响应时间（毫秒）
     */
    public long assertApiRespondsWithinTimeoutMillis(String url, long timeoutMillis, boolean verbose) {
        try {
            long start = System.nanoTime();  // 记录开始时间

            String response = this.restTemplate.getForObject(url, String.class);

            long end = System.nanoTime();  // 记录结束时间
            long durationMillis = (end - start) / 1_000_000;
            if (verbose) {
                System.out.println("API Response: " + response);
                System.out.println("Response time: " + durationMillis + " ms");
            }

            if (durationMillis >= timeoutMillis) {
                Assertions.fail("API did not respond within " + timeoutMillis + " milliseconds, url: " +url);
            }

            return durationMillis;
        } catch (Exception e) {
            Assertions.fail("Connection refused when trying to access: " + url);
        }
        return -1;
    }
}
