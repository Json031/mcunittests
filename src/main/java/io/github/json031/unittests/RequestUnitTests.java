package io.github.json031.unittests;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class is used for unit testing api request.
 */
public class RequestUnitTests {

    /**
     * 单例实例
     */
    private static final RequestUnitTests INSTANCE = new RequestUnitTests();

    /**
     * 私有 RestTemplate 实例（默认配置）
     */
    private final RestTemplate restTemplate;

    /**
     * 可配置的 RestTemplate 实例
     */
    private RestTemplate configurableRestTemplate;

    /**
     * 默认超时时间（毫秒）
     */
    private static final int DEFAULT_TIMEOUT = 30000; // 30秒

    /**
     * 私有构造函数
     */
    private RequestUnitTests() {
        this.restTemplate = new RestTemplate();
        this.configurableRestTemplate = createDefaultRestTemplate();
    }

    /**
     * 获取单例
     */
    public static RequestUnitTests getInstance() {
        return INSTANCE;
    }

    /**
     * 创建默认配置的 RestTemplate
     */
    private static RestTemplate createDefaultRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(DEFAULT_TIMEOUT);
        factory.setReadTimeout(DEFAULT_TIMEOUT);
        return new RestTemplate(factory);
    }

    /**
     * 配置自定义超时时间
     * @param connectTimeout 连接超时（毫秒）
     * @param readTimeout    读取超时（毫秒）
     */
    public void configureTimeout(int connectTimeout, int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        this.configurableRestTemplate = new RestTemplate(factory);
    }

    /**
     * 配置自定义 RestTemplate
     * @param customRestTemplate 自定义的 RestTemplate 实例
     */
    public void configureRestTemplate(RestTemplate customRestTemplate) {
        this.configurableRestTemplate = customRestTemplate;
    }

    /**
     * 重置为默认配置
     */
    public void resetToDefault() {
        this.configurableRestTemplate = createDefaultRestTemplate();
    }

    /**
     * 请求 API（使用默认 RestTemplate）
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
        return executeRequest(INSTANCE.restTemplate, url, method, params, headers, verbose, null);
    }

    /**
     * 请求 API（使用可配置的 RestTemplate）
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param verbose       是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithConfigurableRestTemplate(String url,
                                                                             HttpMethod method,
                                                                             Map<String, Object> params,
                                                                             Map<String, String> headers,
                                                                             boolean verbose) {
        return executeRequest(INSTANCE.configurableRestTemplate, url, method, params, headers, verbose, null);
    }

    /**
     * 发送GET请求
     *
     * @param url     请求地址
     * @param params  查询参数
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult get(String url,
                                             Map<String, Object> params,
                                             Map<String, String> headers,
                                             boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.GET, params, headers, verbose);
    }

    /**
     * 发送POST请求
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult post(String url,
                                              Map<String, Object> body,
                                              Map<String, String> headers,
                                              boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.POST, body, headers, verbose);
    }

    /**
     * 发送PUT请求
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult put(String url,
                                             Map<String, Object> body,
                                             Map<String, String> headers,
                                             boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.PUT, body, headers, verbose);
    }

    /**
     * 发送DELETE请求
     *
     * @param url     请求地址
     * @param params  查询参数
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult delete(String url,
                                                Map<String, Object> params,
                                                Map<String, String> headers,
                                                boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.DELETE, params, headers, verbose);
    }

    /**
     * 发送PATCH请求
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult patch(String url,
                                               Map<String, Object> body,
                                               Map<String, String> headers,
                                               boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.PATCH, body, headers, verbose);
    }

    /**
     * 发送HEAD请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult head(String url,
                                              Map<String, String> headers,
                                              boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.HEAD, null, headers, verbose);
    }

    /**
     * 发送OPTIONS请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult options(String url,
                                                 Map<String, String> headers,
                                                 boolean verbose) {
        return requestWitRestTemplate(url, HttpMethod.OPTIONS, null, headers, verbose);
    }

    /**
     * 发送带JSON body的POST请求
     *
     * @param url        请求地址
     * @param jsonBody   JSON字符串
     * @param headers    请求头
     * @param verbose    是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult postJson(String url,
                                                  String jsonBody,
                                                  Map<String, String> headers,
                                                  boolean verbose) {
        return executeRequestWithStringBody(INSTANCE.restTemplate, url, HttpMethod.POST, jsonBody,
                headers, verbose, MediaType.APPLICATION_JSON);
    }

    /**
     * 发送带XML body的POST请求
     *
     * @param url        请求地址
     * @param xmlBody    XML字符串
     * @param headers    请求头
     * @param verbose    是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult postXml(String url,
                                                 String xmlBody,
                                                 Map<String, String> headers,
                                                 boolean verbose) {
        return executeRequestWithStringBody(INSTANCE.restTemplate, url, HttpMethod.POST, xmlBody,
                headers, verbose, MediaType.APPLICATION_XML);
    }

    /**
     * 上传文件
     *
     * @param url        请求地址
     * @param file       文件
     * @param fieldName  表单字段名
     * @param headers    请求头
     * @param verbose    是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult uploadFile(String url,
                                                    File file,
                                                    String fieldName,
                                                    Map<String, String> headers,
                                                    boolean verbose) {
        if (!DataUnitTests.isValidUrl(url)) {
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }

        org.springframework.util.LinkedMultiValueMap<String, Object> body =
                new org.springframework.util.LinkedMultiValueMap<>();
        body.add(fieldName, new org.springframework.core.io.FileSystemResource(file));

        HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, httpHeaders);

        return executeRequestWithEntity(INSTANCE.restTemplate, url, HttpMethod.POST, entity, verbose);
    }

    /**
     * 下载文件
     *
     * @param url        请求地址
     * @param savePath   保存路径
     * @param headers    请求头
     * @param verbose    是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult downloadFile(String url,
                                                      String savePath,
                                                      Map<String, String> headers,
                                                      boolean verbose) {
        if (!DataUnitTests.isValidUrl(url)) {
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        Instant startTime = Instant.now();
        long startNano = System.nanoTime();

        ResponseEntity<byte[]> response = null;
        String errorMessage = null;
        int statusCode = 0;
        boolean isSuccess = false;
        int responseSizeBytes = 0;
        long threadId = Thread.currentThread().getId();

        try {
            response = INSTANCE.restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            if (response != null && response.getBody() != null) {
                statusCode = response.getStatusCodeValue();
                isSuccess = response.getStatusCode().is2xxSuccessful();
                responseSizeBytes = response.getBody().length;

                // 保存文件
                Files.write(new File(savePath).toPath(), response.getBody());

                if (verbose) {
                    System.out.println("File downloaded successfully to: " + savePath);
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (verbose) {
                System.out.println("Download failed: " + errorMessage);
            }
        }

        long endNano = System.nanoTime();
        long durationMillis = (endNano - startNano) / 1_000_000;
        Instant endTime = Instant.now();

        // 将byte[]响应转换为String类型（用于兼容）
        ResponseEntity<String> stringResponse = null;
        if (response != null) {
            stringResponse = new ResponseEntity<>(
                    "Binary file downloaded (" + responseSizeBytes + " bytes)",
                    response.getHeaders(),
                    response.getStatusCode()
            );
        }

        return new RequestUnitTestsResult(
                durationMillis,
                stringResponse,
                statusCode,
                isSuccess,
                errorMessage,
                url,
                HttpMethod.GET.name(),
                responseSizeBytes,
                startTime,
                endTime,
                threadId
        );
    }

    /**
     * 异步请求
     *
     * @param url     请求地址
     * @param method  请求方式
     * @param params  请求参数
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return CompletableFuture包装的响应数据
     */
    public static CompletableFuture<RequestUnitTestsResult> requestAsync(String url,
                                                                         HttpMethod method,
                                                                         Map<String, Object> params,
                                                                         Map<String, String> headers,
                                                                         boolean verbose) {
        return CompletableFuture.supplyAsync(() ->
                requestWitRestTemplate(url, method, params, headers, verbose)
        );
    }

    /**
     * 带重试机制的请求
     *
     * @param url           请求地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param maxRetries    最大重试次数
     * @param retryDelay    重试间隔（毫秒）
     * @param verbose       是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithRetry(String url,
                                                          HttpMethod method,
                                                          Map<String, Object> params,
                                                          Map<String, String> headers,
                                                          int maxRetries,
                                                          long retryDelay,
                                                          boolean verbose) {
        RequestUnitTestsResult result = null;
        int attempt = 0;

        while (attempt <= maxRetries) {
            result = requestWitRestTemplate(url, method, params, headers, verbose);

            if (result != null && result.isSuccess) {
                if (verbose && attempt > 0) {
                    System.out.println("Request succeeded on attempt " + (attempt + 1));
                }
                return result;
            }

            attempt++;
            if (attempt <= maxRetries) {
                if (verbose) {
                    System.out.println("Request failed, retrying in " + retryDelay + "ms... (attempt " +
                            (attempt + 1) + "/" + (maxRetries + 1) + ")");
                }
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (verbose) {
            System.out.println("Request failed after " + (maxRetries + 1) + " attempts");
        }
        return result;
    }

    /**
     * 带指数退避的重试请求
     *
     * @param url               请求地址
     * @param method            请求方式
     * @param params            请求参数
     * @param headers           请求头
     * @param maxRetries        最大重试次数
     * @param initialDelayMs    初始延迟（毫秒）
     * @param maxDelayMs        最大延迟（毫秒）
     * @param verbose           是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithExponentialBackoff(String url,
                                                                       HttpMethod method,
                                                                       Map<String, Object> params,
                                                                       Map<String, String> headers,
                                                                       int maxRetries,
                                                                       long initialDelayMs,
                                                                       long maxDelayMs,
                                                                       boolean verbose) {
        RequestUnitTestsResult result = null;
        long delay = initialDelayMs;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            result = requestWitRestTemplate(url, method, params, headers, verbose);

            if (result != null && result.isSuccess) {
                if (verbose && attempt > 0) {
                    System.out.println("Request succeeded on attempt " + (attempt + 1));
                }
                return result;
            }

            if (attempt < maxRetries) {
                if (verbose) {
                    System.out.println("Request failed, retrying in " + delay + "ms... (attempt " +
                            (attempt + 2) + "/" + (maxRetries + 1) + ")");
                }
                try {
                    Thread.sleep(delay);
                    delay = Math.min(delay * 2, maxDelayMs); // 指数退避
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (verbose) {
            System.out.println("Request failed after " + (maxRetries + 1) + " attempts");
        }
        return result;
    }

    /**
     * 批量请求（并行）
     *
     * @param urls    URL列表
     * @param method  请求方式
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据列表
     */
    public static List<RequestUnitTestsResult> batchRequestParallel(List<String> urls,
                                                                    HttpMethod method,
                                                                    Map<String, String> headers,
                                                                    boolean verbose) {
        List<CompletableFuture<RequestUnitTestsResult>> futures = new ArrayList<>();

        for (String url : urls) {
            futures.add(requestAsync(url, method, null, headers, verbose));
        }

        // 等待所有请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 收集结果
        List<RequestUnitTestsResult> results = new ArrayList<>();
        for (CompletableFuture<RequestUnitTestsResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                if (verbose) {
                    System.out.println("Failed to get result: " + e.getMessage());
                }
            }
        }

        return results;
    }

    /**
     * 批量请求（串行）
     *
     * @param urls    URL列表
     * @param method  请求方式
     * @param headers 请求头
     * @param verbose 是否打印响应
     * @return 响应数据列表
     */
    public static List<RequestUnitTestsResult> batchRequestSequential(List<String> urls,
                                                                      HttpMethod method,
                                                                      Map<String, String> headers,
                                                                      boolean verbose) {
        List<RequestUnitTestsResult> results = new ArrayList<>();

        for (String url : urls) {
            RequestUnitTestsResult result = requestWitRestTemplate(url, method, null, headers, verbose);
            results.add(result);
        }

        return results;
    }

    /**
     * 带超时控制的请求
     *
     * @param url        请求地址
     * @param method     请求方式
     * @param params     请求参数
     * @param headers    请求头
     * @param timeoutMs  超时时间（毫秒）
     * @param verbose    是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithTimeout(String url,
                                                            HttpMethod method,
                                                            Map<String, Object> params,
                                                            Map<String, String> headers,
                                                            long timeoutMs,
                                                            boolean verbose) {
        // 创建临时的 RestTemplate 配置
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) timeoutMs);
        factory.setReadTimeout((int) timeoutMs);
        RestTemplate tempRestTemplate = new RestTemplate(factory);

        return executeRequest(tempRestTemplate, url, method, params, headers, verbose, null);
    }

    /**
     * 发送带Basic Auth的请求
     *
     * @param url      请求地址
     * @param method   请求方式
     * @param params   请求参数
     * @param username 用户名
     * @param password 密码
     * @param verbose  是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithBasicAuth(String url,
                                                              HttpMethod method,
                                                              Map<String, Object> params,
                                                              String username,
                                                              String password,
                                                              boolean verbose) {
        Map<String, String> headers = new HashMap<>();
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", "Basic " + encodedAuth);

        return requestWitRestTemplate(url, method, params, headers, verbose);
    }

    /**
     * 发送带Bearer Token的请求
     *
     * @param url     请求地址
     * @param method  请求方式
     * @param params  请求参数
     * @param token   Bearer token
     * @param verbose 是否打印响应
     * @return 响应数据
     */
    public static RequestUnitTestsResult requestWithBearerToken(String url,
                                                                HttpMethod method,
                                                                Map<String, Object> params,
                                                                String token,
                                                                boolean verbose) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return requestWitRestTemplate(url, method, params, headers, verbose);
    }

    /**
     * 核心执行方法 - 执行HTTP请求
     */
    private static RequestUnitTestsResult executeRequest(RestTemplate template,
                                                         String url,
                                                         HttpMethod method,
                                                         Map<String, Object> params,
                                                         Map<String, String> headers,
                                                         boolean verbose,
                                                         MediaType contentType) {
        //校验 URL 是否合法
        if (!DataUnitTests.isValidUrl(url)) {
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        if (contentType != null) {
            httpHeaders.setContentType(contentType);
        }
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
            // POST/PUT/PATCH 请求体（可为 null）
            entity = new HttpEntity<>(params, httpHeaders);
        }

        return executeRequestWithEntity(template, finalUrl, method, entity, verbose);
    }

    /**
     * 使用字符串body执行请求
     */
    private static RequestUnitTestsResult executeRequestWithStringBody(RestTemplate template,
                                                                       String url,
                                                                       HttpMethod method,
                                                                       String body,
                                                                       Map<String, String> headers,
                                                                       boolean verbose,
                                                                       MediaType contentType) {
        if (!DataUnitTests.isValidUrl(url)) {
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        if (contentType != null) {
            httpHeaders.setContentType(contentType);
        }
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
        return executeRequestWithEntity(template, url, method, entity, verbose);
    }

    /**
     * 使用HttpEntity执行请求
     */
    private static RequestUnitTestsResult executeRequestWithEntity(RestTemplate template,
                                                                   String url,
                                                                   HttpMethod method,
                                                                   HttpEntity<?> entity,
                                                                   boolean verbose) {
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
            response = template.exchange(url, method, entity, String.class);

            if (response != null) {
                statusCode = response.getStatusCodeValue();
                isSuccess = response.getStatusCode().is2xxSuccessful();
                if (response.getBody() != null) {
                    responseSizeBytes = response.getBody().getBytes(StandardCharsets.UTF_8).length;
                }
            }

            if (verbose && response != null) {
                System.out.println("API Response [" + statusCode + "]: " + response.getBody());
            }

        } catch (RestClientException e) {
            errorMessage = e.getMessage();
            // 尝试从异常中提取状态码
            if (e instanceof org.springframework.web.client.HttpStatusCodeException) {
                statusCode = ((org.springframework.web.client.HttpStatusCodeException) e).getRawStatusCode();
            }
            if (verbose) {
                System.out.println("API call failed for: " + url + " with error: " + errorMessage);
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (verbose) {
                System.out.println("Unexpected error for: " + url + " - " + errorMessage);
            }
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
                url,
                methodStr,
                responseSizeBytes,
                startTime,
                endTime,
                threadId
        );
    }

    /**
     * 请求配置构建器
     */
    public static class RequestConfigBuilder {
        private String url;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, Object> params;
        private Map<String, String> headers;
        private boolean verbose = false;
        private int maxRetries = 0;
        private long retryDelay = 1000;
        private Long timeoutMs;
        private String bearerToken;
        private String basicAuthUsername;
        private String basicAuthPassword;

        public RequestConfigBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestConfigBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestConfigBuilder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public RequestConfigBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestConfigBuilder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public RequestConfigBuilder retry(int maxRetries, long retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
            return this;
        }

        public RequestConfigBuilder timeout(long timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public RequestConfigBuilder bearerToken(String token) {
            this.bearerToken = token;
            return this;
        }

        public RequestConfigBuilder basicAuth(String username, String password) {
            this.basicAuthUsername = username;
            this.basicAuthPassword = password;
            return this;
        }

        public RequestUnitTestsResult execute() {
            // 添加认证header
            if (headers == null) {
                headers = new HashMap<>();
            }

            if (bearerToken != null) {
                headers.put("Authorization", "Bearer " + bearerToken);
            } else if (basicAuthUsername != null && basicAuthPassword != null) {
                String auth = basicAuthUsername + ":" + basicAuthPassword;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                headers.put("Authorization", "Basic " + encodedAuth);
            }

            // 根据配置执行请求
            if (maxRetries > 0) {
                return requestWithRetry(url, method, params, headers, maxRetries, retryDelay, verbose);
            } else if (timeoutMs != null) {
                return requestWithTimeout(url, method, params, headers, timeoutMs, verbose);
            } else {
                return requestWitRestTemplate(url, method, params, headers, verbose);
            }
        }
    }

    /**
     * 创建请求配置构建器
     */
    public static RequestConfigBuilder builder() {
        return new RequestConfigBuilder();
    }
}