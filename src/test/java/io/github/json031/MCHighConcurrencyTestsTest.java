package io.github.json031;

import io.github.json031.JavaBean.HighConcurrencyResult;
import io.github.json031.apitests.MCHighConcurrencyTests;
import io.github.json031.apitests.MCApiTests;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MCHighConcurrencyTestsTest {

    @Test
    public void testHighConcurrencyTestWithTimeoutSeconds() {
        // 准备一个假的 MCHighConcurrencyTests 对象，内部的 MCApiTests 被 mock
        MCHighConcurrencyTests tests = new MCHighConcurrencyTests() {
            @Override
            public HighConcurrencyResult highConcurrencyTestWithTimeoutMillis(String url, int threadCount, HttpMethod method,
                                                                              Map<String, Object> params, Map<String, String> headers,
                                                                              long timeoutMillis, boolean verbose) {
                return new HighConcurrencyResult(threadCount, threadCount, 0, 100); // 假设全部成功
            }
        };

        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        HighConcurrencyResult result = tests.highConcurrencyTestWithTimeoutSeconds(
                "https://www.baidu.com",
                5,
                HttpMethod.GET,
                params,
                null,
                2, // 2秒超时
                false
        );

        assertNotNull(result);
        assertEquals(5, result.total);
        assertEquals(5, result.success);
        assertEquals(0, result.failed);
        assertTrue(result.avgResponseTimeMillis > 0);
        assertTrue(result.avgResponseTimeMillis < 2000);
    }

    @Test
    public void testHighConcurrencyTestWithTimeoutMillis_RealisticMock() {
        // 这里对 MCApiTests 进行 mock（为了不真的去请求网络）
        MCHighConcurrencyTests tests = new MCHighConcurrencyTests();
        tests.mcApiTests = Mockito.mock(MCApiTests.class);

        try {
            // 设定 mock 返回固定的响应时间
            Mockito.when(tests.mcApiTests.assertApiRespondsWithinTimeoutMillis(
                    Mockito.any(),
                    Mockito.nullable(HttpMethod.class),
                    Mockito.nullable(Map.class),
                    Mockito.nullable(Map.class),
                    Mockito.anyLong(),
                    Mockito.anyBoolean()
            )).thenReturn(true); // true

            Map<String, Object> params = new HashMap<>();
            params.put("test", "value");

            HighConcurrencyResult result = tests.highConcurrencyTestWithTimeoutMillis(
                    "http://example.com/api",
                    3,
                    HttpMethod.GET,
                    params,
                    null,
                    1000,
                    false
            );

            assertNotNull(result);
            assertEquals(3, result.total);
            assertEquals(3, result.success);
            assertEquals(0, result.failed);
            assertTrue(result.avgResponseTimeMillis > 0);
            assertTrue(result.avgResponseTimeMillis < 15000);
        } catch (Exception e) {
            fail("Exception thrown in highConcurrencyTestWithTimeoutMillis: " + e.getMessage());
        }
    }
}
