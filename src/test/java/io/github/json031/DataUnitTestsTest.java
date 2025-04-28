package io.github.json031;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class DataUnitTestsTest {

    @Test
    public void testIsValidJSON() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = ResponseEntity.ok()
                .headers(headers)
                .body("{\"key\":\"value\"}");

        assertTrue(DataUnitTests.isValidJSON(response));
    }

    @Test
    public void testIsValidUrl() {
        DataUnitTests.isValidUrl("https://www.baidu.com");
    }

    @Test
    public void testIsJSONContentType_ResponseEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = ResponseEntity.ok()
                .headers(headers)
                .body("{\"key\":\"value\"}");

        assertTrue(DataUnitTests.isJSONContentType(response));
    }

    @Test
    public void testIsJSONContentType_MediaType() {
        assertTrue(DataUnitTests.isJSONContentType(MediaType.APPLICATION_JSON));
        assertFalse(DataUnitTests.isJSONContentType(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testRequestUnitTests() {
        String url = "https://www.baidu.com";
        // 调用请求方法
        RequestUnitTestsResult result = RequestUnitTests.requestWitRestTemplate(url, HttpMethod.GET, null, null, true);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.durationMillis < 2000);
        assertEquals(200, result.response.getStatusCode().value());
    }
}

