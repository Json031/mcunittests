package io.github.json031.apitests;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.MCUnitTests;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.springframework.http.*;

import java.util.Map;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is used for unit testing api.
 */
public class MCApiTests {

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
    public boolean assertApiRespondsWithinTimeout(String url,
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
    public boolean assertApiRespondsWithinTimeoutMillis(String url,
                                                        HttpMethod method,
                                                        Map<String, Object> params,
                                                        Map<String, String> headers,
                                                        long timeoutMillis,
                                                        boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        return DataUnitTests.withinTimeOut(result, timeoutMillis);
    }

    /**
     * 通用测试方法，验证给定 API 是否返回有效json格式数据。
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param verbose       是否打印响应
     */
    public boolean testApiReturnsValidJson(String url,
                                           HttpMethod method,
                                           Map<String, Object> params,
                                           Map<String, String> headers,
                                           boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.print("API did not respond valid json, url: " + url);
            }
            return false;
        } else {
            ResponseEntity<String> response = result.response;
            return DataUnitTests.isValidJSON(response);
        }
    }


    /**
     * 通用测试方法，验证 API 是否正常运行。
     *
     * @param url           完整的 API 地址（包括 http/https）
     * @param method        请求方式（GET / POST）
     * @param params        请求参数（POST body 或 GET 查询参数）
     * @param headers       请求头（可选）
     * @param verbose       是否打印响应
     */
    public boolean testApiHealth(String url,
                                 HttpMethod method,
                                 Map<String, Object> params,
                                 Map<String, String> headers,
                                 long timeoutMillis,
                                 boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.print("API did not respond : " + url);
            }
            return false;
        } else {
            ResponseEntity<String> response = result.response;
            boolean isValidContentType = true;
            if (response.getHeaders().getContentType() == MediaType.APPLICATION_JSON || response.getHeaders().getContentType().getSubtype().equals(MediaType.APPLICATION_JSON.getSubtype())) {
                isValidContentType = DataUnitTests.isValidJSON(response);
            }
            boolean isWithinTimeOut = DataUnitTests.withinTimeOut(result, timeoutMillis);
            return isWithinTimeOut && isValidContentType;
        }
    }

    /**
     * 测试 API 是否返回预期的 HTTP 状态码
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param expectedStatus 期望的 HTTP 状态码
     * @param verbose       是否打印响应
     * @return 是否返回预期状态码
     */
    public boolean testApiReturnsExpectedStatus(String url,
                                                HttpMethod method,
                                                Map<String, Object> params,
                                                Map<String, String> headers,
                                                HttpStatus expectedStatus,
                                                boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond, url: " + url);
            }
            return false;
        }

        HttpStatus actualStatus = result.response.getStatusCode();
        boolean matches = actualStatus.equals(expectedStatus);

        if (!matches && MCUnitTests.getInstance().verbose) {
            System.out.println("Expected status: " + expectedStatus + ", but got: " + actualStatus);
        }

        return matches;
    }

    /**
     * 测试 API 响应体是否包含指定的文本内容
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param expectedContent 期望包含的内容
     * @param verbose       是否打印响应
     * @return 是否包含预期内容
     */
    public boolean testApiResponseContains(String url,
                                           HttpMethod method,
                                           Map<String, Object> params,
                                           Map<String, String> headers,
                                           String expectedContent,
                                           boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null || result.response.getBody() == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond with body, url: " + url);
            }
            return false;
        }

        String body = result.response.getBody();
        boolean contains = body.contains(expectedContent);

        if (!contains && MCUnitTests.getInstance().verbose) {
            System.out.println("Response body does not contain expected content: " + expectedContent);
        }

        return contains;
    }

    /**
     * 测试 API 响应头是否包含指定的 header
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param headerName    期望的响应头名称
     * @param headerValue   期望的响应头值（可为null，仅验证存在）
     * @param verbose       是否打印响应
     * @return 是否包含预期响应头
     */
    public boolean testApiResponseHeader(String url,
                                         HttpMethod method,
                                         Map<String, Object> params,
                                         Map<String, String> headers,
                                         String headerName,
                                         String headerValue,
                                         boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond, url: " + url);
            }
            return false;
        }

        HttpHeaders responseHeaders = result.response.getHeaders();
        if (!responseHeaders.containsKey(headerName)) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Response does not contain header: " + headerName);
            }
            return false;
        }

        if (headerValue != null) {
            List<String> actualValues = responseHeaders.get(headerName);
            boolean matches = actualValues != null && actualValues.contains(headerValue);
            if (!matches && MCUnitTests.getInstance().verbose) {
                System.out.println("Header " + headerName + " value mismatch. Expected: " + headerValue + ", got: " + actualValues);
            }
            return matches;
        }

        return true;
    }

    /**
     * 测试 API 响应体是否符合自定义断言条件
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param assertion     自定义断言（接收响应体字符串）
     * @param verbose       是否打印响应
     * @return 是否满足断言条件
     */
    public boolean testApiWithCustomAssertion(String url,
                                              HttpMethod method,
                                              Map<String, Object> params,
                                              Map<String, String> headers,
                                              Predicate<String> assertion,
                                              boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null || result.response.getBody() == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond with body, url: " + url);
            }
            return false;
        }

        try {
            return assertion.test(result.response.getBody());
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Assertion failed with exception: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 测试 API 是否返回指定的 Content-Type
     *
     * @param url              完整的 API 地址
     * @param method           请求方式
     * @param params           请求参数
     * @param headers          请求头
     * @param expectedMediaType 期望的 MediaType
     * @param verbose          是否打印响应
     * @return 是否返回预期的 Content-Type
     */
    public boolean testApiContentType(String url,
                                      HttpMethod method,
                                      Map<String, Object> params,
                                      Map<String, String> headers,
                                      MediaType expectedMediaType,
                                      boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond, url: " + url);
            }
            return false;
        }

        MediaType actualMediaType = result.response.getHeaders().getContentType();
        boolean matches = expectedMediaType.isCompatibleWith(actualMediaType);

        if (!matches && MCUnitTests.getInstance().verbose) {
            System.out.println("Expected Content-Type: " + expectedMediaType + ", but got: " + actualMediaType);
        }

        return matches;
    }

    /**
     * 测试 API 响应体大小是否在指定范围内
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param minSize       最小字节数（-1 表示不限制）
     * @param maxSize       最大字节数（-1 表示不限制）
     * @param verbose       是否打印响应
     * @return 响应体大小是否在范围内
     */
    public boolean testApiResponseSize(String url,
                                       HttpMethod method,
                                       Map<String, Object> params,
                                       Map<String, String> headers,
                                       long minSize,
                                       long maxSize,
                                       boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null || result.response.getBody() == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond with body, url: " + url);
            }
            return false;
        }

        long actualSize = result.response.getBody().getBytes().length;
        boolean withinRange = true;

        if (minSize > 0 && actualSize < minSize) {
            withinRange = false;
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Response size " + actualSize + " is less than minimum " + minSize);
            }
        }

        if (maxSize > 0 && actualSize > maxSize) {
            withinRange = false;
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Response size " + actualSize + " exceeds maximum " + maxSize);
            }
        }

        return withinRange;
    }

    /**
     * 测试 API 是否返回空响应体
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param verbose       是否打印响应
     * @return 响应体是否为空
     */
    public boolean testApiReturnsEmptyBody(String url,
                                           HttpMethod method,
                                           Map<String, Object> params,
                                           Map<String, String> headers,
                                           boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null || result.response == null) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("API did not respond, url: " + url);
            }
            return false;
        }

        String body = result.response.getBody();
        return body == null || body.trim().isEmpty();
    }

    /**
     * 批量测试多个 API 端点的健康状态
     *
     * @param endpoints     API 端点列表
     * @param method        请求方式
     * @param headers       请求头（可选）
     * @param timeoutMillis 超时时间（毫秒）
     * @param verbose       是否打印响应
     * @return 所有 API 是否都正常
     */
    public boolean testMultipleApisHealth(List<String> endpoints,
                                          HttpMethod method,
                                          Map<String, String> headers,
                                          long timeoutMillis,
                                          boolean verbose) {
        boolean allHealthy = true;

        for (String endpoint : endpoints) {
            boolean healthy = testApiHealth(endpoint, method, null, headers, timeoutMillis, verbose);
            if (!healthy) {
                allHealthy = false;
                if (MCUnitTests.getInstance().verbose) {
                    System.out.println("Unhealthy endpoint: " + endpoint);
                }
            }
        }

        return allHealthy;
    }

    /**
     * 测试 API 在并发请求下的稳定性
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param concurrency   并发请求数
     * @param verbose       是否打印响应
     * @return 所有并发请求是否都成功（200 状态码）
     */
    public boolean testApiConcurrency(String url,
                                      HttpMethod method,
                                      Map<String, Object> params,
                                      Map<String, String> headers,
                                      int concurrency,
                                      boolean verbose) {
        final int[] successCount = {0};
        final int[] failureCount = {0};

        Thread[] threads = new Thread[concurrency];

        for (int i = 0; i < concurrency; i++) {
            threads[i] = new Thread(() -> {
                try {
                    RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, false);
                    if (result != null && result.response != null && result.response.getStatusCode().is2xxSuccessful()) {
                        synchronized (successCount) {
                            successCount[0]++;
                        }
                    } else {
                        synchronized (failureCount) {
                            failureCount[0]++;
                        }
                    }
                } catch (Exception e) {
                    synchronized (failureCount) {
                        failureCount[0]++;
                    }
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (verbose || MCUnitTests.getInstance().verbose) {
            System.out.println("Concurrency test results - Success: " + successCount[0] + ", Failure: " + failureCount[0]);
        }

        return failureCount[0] == 0;
    }

    /**
     * 测试 API 是否正确处理错误的请求参数
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param invalidParams 无效的请求参数
     * @param headers       请求头
     * @param expectedStatus 期望的错误状态码（如 400, 422）
     * @param verbose       是否打印响应
     * @return 是否返回预期的错误状态码
     */
    public boolean testApiErrorHandling(String url,
                                        HttpMethod method,
                                        Map<String, Object> invalidParams,
                                        Map<String, String> headers,
                                        HttpStatus expectedStatus,
                                        boolean verbose) {
        return testApiReturnsExpectedStatus(url, method, invalidParams, headers, expectedStatus, verbose);
    }

    /**
     * 获取 API 的实际响应时间（毫秒）
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param verbose       是否打印响应
     * @return 响应时间（毫秒），失败返回 -1
     */
    public long getApiResponseTime(String url,
                                   HttpMethod method,
                                   Map<String, Object> params,
                                   Map<String, String> headers,
                                   boolean verbose) {
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, method, params, headers, verbose);
        if (result == null) {
            return -1;
        }
        return result.durationMillis;
    }

    /**
     * 测试 API 的平均响应时间
     *
     * @param url           完整的 API 地址
     * @param method        请求方式
     * @param params        请求参数
     * @param headers       请求头
     * @param iterations    测试次数
     * @param maxAvgTime    最大平均响应时间（毫秒）
     * @param verbose       是否打印响应
     * @return 平均响应时间是否在限制内
     */
    public boolean testApiAverageResponseTime(String url,
                                              HttpMethod method,
                                              Map<String, Object> params,
                                              Map<String, String> headers,
                                              int iterations,
                                              long maxAvgTime,
                                              boolean verbose) {
        long totalTime = 0;
        int successCount = 0;

        for (int i = 0; i < iterations; i++) {
            long responseTime = getApiResponseTime(url, method, params, headers, false);
            if (responseTime > 0) {
                totalTime += responseTime;
                successCount++;
            }
        }

        if (successCount == 0) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("No successful requests in average response time test");
            }
            return false;
        }

        long avgTime = totalTime / successCount;
        boolean withinLimit = avgTime <= maxAvgTime;

        if (verbose || MCUnitTests.getInstance().verbose) {
            System.out.println("Average response time: " + avgTime + "ms (max: " + maxAvgTime + "ms, iterations: " + successCount + ")");
        }

        return withinLimit;
    }
}