package io.github.json031;

import io.github.json031.JavaBean.HighConcurrencyResult;
import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.apitests.MCApiTests;
import io.github.json031.apitests.MCHighConcurrencyTests;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


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
        RequestUnitTestsResult result = RequestUnitTests.getInstance().requestWitRestTemplate(url, HttpMethod.GET, param, null, false);

        //模拟api请求异常情况
        String fakeUrl = "http://localhost:9999/non-existent-endpoint?wd=s";
        HttpMethod method = HttpMethod.GET;
        // 这个端口很可能没有服务运行，会连接失败从而抛出异常
        Assertions.assertThrows(AssertionError.class, () -> {
            Map<String, Object> param2 = new HashMap<>();
            RequestUnitTests.requestWitRestTemplate(fakeUrl, method, param2, null, true);
        });
    }

    @Test
    public void testMCHighConcurrencyTests() {
        MCHighConcurrencyTests mcHighConcurrencyTests = new MCHighConcurrencyTests();
        Map<String, Object> param = new HashMap<>();
        param.put("wd", "hello");
        String url = "https://www.baidu.com";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "TestValue");
        HighConcurrencyResult result = mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(url, 10, HttpMethod.GET, param,headers, 1, true);
        assertTrue(result.failed == result.total);
        assertFalse(result.toString().isEmpty());

        //模拟api请求异常情况
        mcHighConcurrencyTests.mcApiTests = Mockito.mock(MCApiTests.class);
        Mockito.when(mcHighConcurrencyTests.mcApiTests.assertApiRespondsWithinTimeoutMillis(
                Mockito.anyString(),
                Mockito.nullable(HttpMethod.class),
                Mockito.nullable(Map.class),
                Mockito.nullable(Map.class),
                Mockito.anyLong(),
                Mockito.anyBoolean()
        )).thenThrow(new RuntimeException("Simulated failure"));
        mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(url, 10, HttpMethod.GET, param,null, 1, true);
        assertTrue(result.failed == result.total);
    }

    @Test
    public void testDataUnitTests() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = ResponseEntity.ok()
                .headers(headers)
                .body("key}");
        RequestUnitTestsResult result = new RequestUnitTestsResult(10, response);
        assertFalse(DataUnitTests.isValidJSON(response));
        assertFalse(DataUnitTests.withinTimeOut(result, 0));
        assertFalse(DataUnitTests.isValidUrl("s"));
        assertFalse(DataUnitTests.isValidUrl("ftp://invalid.com"));
        DataUnitTests dataUnitTests = new DataUnitTests();

        MCUnitTests.getInstance().verbose = false;
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response2 = ResponseEntity.ok()
                .headers(headers2)
                .body("key}");
        RequestUnitTestsResult result2 = new RequestUnitTestsResult(10, response2);
        assertFalse(DataUnitTests.isValidJSON(response2));
        assertFalse(DataUnitTests.withinTimeOut(result2, 0));
        assertFalse(DataUnitTests.isValidUrl("s"));
        assertFalse(DataUnitTests.isValidUrl("ftp://invalid.com"));
        MediaType mediaType = null;
        assertFalse(DataUnitTests.isJSONContentType(mediaType));
    }

    @Test
    public void testMCApiTests() {
        MCApiTests mcApiTests = new MCApiTests();
        Map<String, Object> param = new HashMap<>();
        param.put("wd", "hello");
        String url = "https://www.baidu.com";
        assertTrue(mcApiTests.assertApiRespondsWithinTimeout(url, HttpMethod.GET, param, null, 15, true));
        assertFalse(mcApiTests.assertApiRespondsWithinTimeoutMillis(url, HttpMethod.GET, param, null, 1, true));
        assertFalse(mcApiTests.testApiReturnsValidJson(url, HttpMethod.GET, param, null, true));
        assertFalse(mcApiTests.testApiReturnsValidJson("url", HttpMethod.GET, param, null, true));
        assertFalse(mcApiTests.testApiHealth(url, HttpMethod.GET, param, null, 1, true));
        assertFalse(mcApiTests.testApiHealth("url", HttpMethod.GET, param, null, 10000, true));
    }
}
