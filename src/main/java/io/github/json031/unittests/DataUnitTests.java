package io.github.json031.unittests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.MCUnitTests;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URL;

/**
 * This class is used for unit testing data.
 */
public class DataUnitTests {

    /**
     * 验证是否为有效json格式数据。
     * @param response     api请求结果
     * @return 是否为有效json格式数据。
     */
    public static <T> Boolean isValidJSON(ResponseEntity<T> response) {
        if (DataUnitTests.isJSONContentType(response)) {
            String json = (String) response.getBody();
            // 用 Jackson 尝试解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.readTree(json);  // 验证数据是否为合法JSON格式数据
                return true;
            } catch (Exception e) {
                if (MCUnitTests.getInstance().verbose) {
                    System.out.print("Invalid JSON data: " + json);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 是否超出期望响应时间
     * @param result        api响应结果
     * @param timeoutMillis        期望响应时间
     * @return 耗时是否超过期望值。
     */
    public static boolean withinTimeOut(RequestUnitTestsResult result, long timeoutMillis) {
        if (result == null) {
            return false;
        } else {
            long durationMillis = result.durationMillis;
            if (durationMillis > timeoutMillis) {
                if (MCUnitTests.getInstance().verbose) {
                    System.out.print("API did not respond within " + timeoutMillis + " ms");
                }
            }
            // 耗时是否超过期望值
            return durationMillis <= timeoutMillis;
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
                if (MCUnitTests.getInstance().verbose) {
                    System.out.print("Invalid URL: " + url);
                }
            }
            return isValidUrl;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.print("Invalid URL: " + url);
            }
            return false;
        }
    }

    /**
     * 获取getMediaType
     * @param result     api请求结果
     * @return MediaType
     */
    public static MediaType getMediaType(RequestUnitTestsResult result) {
        return DataUnitTests.getMediaType(result.response);
    }
    /**
     * 获取getMediaType
     * @param response     api请求结果
     * @return MediaType
     */
    public static <T> MediaType getMediaType(ResponseEntity<T> response) {
        HttpHeaders responseHeaders = response.getHeaders();
        MediaType contentType = responseHeaders.getContentType();
        return contentType;
    }
    /**
     * ContentType是否为application/json
     * @param response     api请求结果
     * @return Boolean 是否为application/json
     */
    public static <T> Boolean isJSONContentType(ResponseEntity<T> response) {
        MediaType contentType = DataUnitTests.getMediaType(response);
        return DataUnitTests.isJSONContentType(contentType);
    }
    /**
     * ContentType是否为application/json
     * @param result     api请求结果
     * @return Boolean 是否为application/json
     */
    public static Boolean isJSONContentType(RequestUnitTestsResult result) {
        MediaType contentType = DataUnitTests.getMediaType(result.response);
        return DataUnitTests.isJSONContentType(contentType);
    }
    /**
     * ContentType是否为application/json
     * @param contentType   api请求结果response.headers.contentType
     * @return Boolean 是否为application/json
     */
    public static Boolean isJSONContentType(MediaType contentType) {
        if (contentType != null) {
            if (MediaType.APPLICATION_JSON.includes(contentType)) {
                return true;
            }
        }
        return false;
    }
}
