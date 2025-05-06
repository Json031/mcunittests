package io.github.json031.unittests;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

/**
 * This class is used for unit testing api request.
 */
public class RequestUnitTests {

    /**
     * 单例实例
     */
    private static final RequestUnitTests INSTANCE = new RequestUnitTests();

    /**
     * 私有 RestTemplate 实例
     */
    private final RestTemplate restTemplate;

    /**
     * 私有构造函数
     */
    private RequestUnitTests() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取单例
     */
    public static RequestUnitTests getInstance() {
        return INSTANCE;
    }

    /**
     * 请求 API
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param verbose       是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWitRestTemplate(String url,
                                                             HttpMethod method,
                                                             Map<String, Object> params,
                                                             Map<String, String> headers,
                                                             boolean verbose) {
        //校验 URL 是否合法
        if (DataUnitTests.isValidUrl(url)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }

            // 请求体
            HttpEntity<?> entity;
            // 请求地址
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

            Instant startTime = Instant.now();
            long startNano = System.nanoTime();

            ResponseEntity<String> response = null;
            String errorMessage = null;
            int statusCode = 0;
            boolean isSuccess = false;
            int responseSizeBytes = 0;
            String methodStr = method.name();
            long threadId = Thread.currentThread().getId();

            try {
                response = INSTANCE.restTemplate.exchange(finalUrl, method, entity, String.class);

                if (response != null) {
                    statusCode = response.getStatusCodeValue();
                    isSuccess = response.getStatusCode().is2xxSuccessful();
                    if (response.getBody() != null) {
                        responseSizeBytes = response.getBody().getBytes(StandardCharsets.UTF_8).length;
                    }
                }

                if (verbose) {
                    System.out.println("API Response: " + (response != null ? response.getBody() : "null"));
                }

            } catch (Exception e) {
                errorMessage = e.getMessage();
                System.out.println("API call failed for: " + url + " with error: " + errorMessage);
            }

            long endNano = System.nanoTime();
            long durationMillis = (endNano - startNano) / 1_000_000;
            Instant endTime = Instant.now();

            return new RequestUnitTestsResult(
                    durationMillis,
                    response,
                    statusCode,
                    isSuccess,
                    errorMessage,
                    finalUrl,
                    methodStr,
                    responseSizeBytes,
                    startTime,
                    endTime,
                    threadId
            );
        }

        return null;
    }
}
