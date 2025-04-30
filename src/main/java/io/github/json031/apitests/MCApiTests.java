package io.github.json031.apitests;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.MCUnitTests;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;

import java.util.Map;

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
            if (response.getHeaders().getContentType() == MediaType.APPLICATION_JSON) {
                isValidContentType = DataUnitTests.isValidJSON(response);
            }
            boolean isWithinTimeOut = DataUnitTests.withinTimeOut(result, timeoutMillis);
            return isWithinTimeOut && isValidContentType;
        }
    }
}
