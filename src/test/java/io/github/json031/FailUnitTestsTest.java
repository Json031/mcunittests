package io.github.json031;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.apitests.MCApiTests;
import io.github.json031.apitests.MCHighConcurrencyTests;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * This unit testing class focuses on testing for failure situations.
 */
public class FailUnitTestsTest {

    @Test
    public void testRequestUnitTests() {
        RequestUnitTestsResult fakeres = RequestUnitTests.getInstance().requestWitRestTemplate("url", HttpMethod.GET, null, null, false);
        assertNull(fakeres);

        Map<String, Object> param = new HashMap<>();
        param.put("wd", "hello");
        String url = "https://www.baidu.com";
        // 调用请求方法
        RequestUnitTestsResult result = RequestUnitTests.getInstance().requestWitRestTemplate(url, HttpMethod.GET, param, null, true);

    }

    @Test
    public void testMCHighConcurrencyTests() {
        MCHighConcurrencyTests mcHighConcurrencyTests = new MCHighConcurrencyTests();
        Map<String, Object> param = new HashMap<>();
        param.put("wd", "hello");
        String url = "https://www.baidu.com";
        mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(url, 10, HttpMethod.GET, param,null, 1, true);
    }

    @Test
    public void testDataUnitTests() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = ResponseEntity.ok()
                .headers(headers)
                .body("key}");
        RequestUnitTestsResult result = new RequestUnitTestsResult(10, response);
        DataUnitTests.isValidJSON(response);
        DataUnitTests.withinTimeOut(result, 0);
        DataUnitTests.isValidUrl("s");
    }

    @Test
    public void testMCApiTests() {
        MCApiTests mcApiTests = new MCApiTests();
        Map<String, Object> param = new HashMap<>();
        param.put("wd", "hello");
        String url = "https://www.baidu.com";
        mcApiTests.assertApiRespondsWithinTimeout(url, HttpMethod.GET, param, null, 10, true);
        mcApiTests.assertApiRespondsWithinTimeoutMillis(url, HttpMethod.GET, param, null, 10000, true);
        mcApiTests.testApiReturnsValidJson(url, HttpMethod.GET, param, null, true);
        mcApiTests.testApiHealth(url, HttpMethod.GET, param, null, 10000, true);
    }
}
