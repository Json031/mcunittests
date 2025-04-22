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
     */
    public void assertApiRespondsWithinTimeout(String url, long timeout) {
        try {
            Assertions.assertTimeout(Duration.ofSeconds(timeout), () -> {
                String response = restTemplate.getForObject(url, String.class);
                System.out.println("API Response: " + response);
            }, "API did not respond within " + timeout + " seconds");
        } catch (Exception e) {
            Assertions.fail("Connection refused when trying to access: " + url);
        }
    }
}
