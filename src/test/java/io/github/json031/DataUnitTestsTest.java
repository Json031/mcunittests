package io.github.json031;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
        RequestUnitTestsResult result = RequestUnitTestsResult.testJsonResult(0, "","{\"key\":\"value\"}", "GET");

        assertTrue(DataUnitTests.isJSONContentType(result.response));
        assertTrue(DataUnitTests.isJSONContentType(result));
        assertTrue(DataUnitTests.getMediaType(result).equals(MediaType.APPLICATION_JSON));
        assertFalse(DataUnitTests.withinTimeOut(null, 0));

        //非JSON类型
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("key", "value");
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<String> response2 = ResponseEntity.ok()
                .headers(headers2)
                .body(formData.toString());
        //非JSON类型返回false
        assertFalse(DataUnitTests.isValidJSON(response2));
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
        RequestUnitTestsResult result = RequestUnitTests.getInstance().requestWitRestTemplate(url, HttpMethod.GET, null, null, true);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.durationMillis < 15000);
        assertEquals(200, result.response.getStatusCode().value());
    }
}

