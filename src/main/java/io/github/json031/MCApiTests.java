package io.github.json031;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

public class MCApiTests {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 通用测试方法，验证给定 API 是否能在 timeout 秒内响应
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param timeout 最大允许超时时间（秒）
     * @param verbose       是否打印响应
     * @return 响应时间（毫秒）
     */
    public long assertApiRespondsWithinTimeout(String url,
                                               HttpMethod method,
                                               Map<String, Object> params,
                                               Map<String, String> headers,
                                               long timeout,
                                               boolean verbose) {
        return this.assertApiRespondsWithinTimeoutMillis(url, method, params, headers, timeout * 1000, verbose);
    }

    /**
     * 通用测试方法，验证给定 API 是否能在 timeoutMillis 内响应。
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param timeoutMillis 超时时间（毫秒）
     * @param verbose       是否打印响应
     * @return 响应时间（毫秒）
     */
    public long assertApiRespondsWithinTimeoutMillis(String url,
                                                     HttpMethod method,
                                                     Map<String, Object> params,
                                                     Map<String, String> headers,
                                                     long timeoutMillis,
                                                     boolean verbose) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }

            HttpEntity<?> entity;
            String finalUrl = url;

            if (method == HttpMethod.GET && params != null && !params.isEmpty()) {
                // 拼接 GET 参数
                StringBuilder queryBuilder = new StringBuilder(url);
                queryBuilder.append(url.contains("?") ? "&" : "?");
                params.forEach((key, value) -> queryBuilder.append(key).append("=").append(value).append("&"));
                finalUrl = queryBuilder.substring(0, queryBuilder.length() - 1); // 去掉最后一个 &
                entity = new HttpEntity<>(httpHeaders);
            } else {
                // POST 请求体（可为 null）
                entity = new HttpEntity<>(params, httpHeaders);
            }

            long start = System.nanoTime();

            ResponseEntity<String> response = restTemplate.exchange(
                    finalUrl,
                    method,
                    entity,
                    String.class
            );

            long end = System.nanoTime();
            long durationMillis = (end - start) / 1_000_000;

            if (verbose) {
                System.out.println("API Response: " + response.getBody());
                System.out.println("Response time: " + durationMillis + " ms");
            }

            if (durationMillis > timeoutMillis) {
                Assertions.fail("API did not respond within " + timeoutMillis + " ms, url: " + finalUrl);
            }

            return durationMillis;

        } catch (Exception e) {
            Assertions.fail("API call failed for: " + url + " with error: " + e.getMessage());
        }

        return -1;
    }
}
