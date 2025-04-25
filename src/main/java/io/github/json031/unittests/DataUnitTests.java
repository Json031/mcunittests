package io.github.json031.unittests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.json031.JavaBean.RequestUnitTestsResult;
import org.junit.jupiter.api.Assertions;

import java.net.URL;

/**
 * This class is used for unit testing data.
 */
public class DataUnitTests {

    /**
     * 验证是否为有效json格式数据。
     * @param json        数据
     * @return 是否为有效json格式数据。
     */
    public static Boolean isValidJSON(String json) {
        // 用 Jackson 尝试解析 JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(json);  // 验证数据是否为合法JSON格式数据
            return true;
        } catch (Exception e) {
            Assertions.fail("Invalid JSON data: " + json);
            return false;
        }
    }

    /**
     * 是否超出期望响应时间
     * @param result        api响应结果
     * @param timeoutMillis        期望响应时间
     * @return 是否为有效json格式数据。
     */
    public static long withinTimeOut(RequestUnitTestsResult result, long timeoutMillis) {
        if (result == null) {
            return -1;
        } else {
            long durationMillis = result.durationMillis;

            // 耗时是否超过期望值，超过期望值则fail
            if (durationMillis > timeoutMillis) {
                Assertions.fail("API did not respond within " + timeoutMillis + " ms");
            }

            return durationMillis;
        }
    }

    /**
     * 校验 URL 是否合法
     * @param url        URL
     * @return URL 是否合法
     */
    public static Boolean isValidUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol();
            Boolean isValidUrl = protocol.equals("http") || protocol.equals("https");
            if (!isValidUrl) {
                Assertions.fail("Invalid URL: " + url);
            }
            return isValidUrl;
        } catch (Exception e) {
            Assertions.fail("Invalid URL: " + url);
            return false;
        }
    }

}
