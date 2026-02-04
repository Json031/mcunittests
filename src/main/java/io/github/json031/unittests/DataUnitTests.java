package io.github.json031.unittests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.MCUnitTests;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is used for unit testing data.
 */
public class DataUnitTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
     * 验证响应体是否为有效的JSON并且可以解析为指定类型
     * @param response     api请求结果
     * @param clazz        目标类型
     * @return 是否可以解析为指定类型
     */
    public static <T, R> Boolean isValidJSONOfType(ResponseEntity<T> response, Class<R> clazz) {
        if (!isValidJSON(response)) {
            return false;
        }

        try {
            String json = (String) response.getBody();
            objectMapper.readValue(json, clazz);
            return true;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("JSON cannot be parsed to type " + clazz.getName() + ": " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 验证JSON是否包含指定的字段
     * @param response     api请求结果
     * @param fieldPath    字段路径（支持嵌套，如 "user.name"）
     * @return 是否包含指定字段
     */
    public static <T> Boolean jsonContainsField(ResponseEntity<T> response, String fieldPath) {
        if (!isValidJSON(response)) {
            return false;
        }

        try {
            String json = (String) response.getBody();
            JsonNode rootNode = objectMapper.readTree(json);

            String[] pathParts = fieldPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String part : pathParts) {
                if (currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error checking field path " + fieldPath + ": " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 验证JSON字段的值是否符合预期
     * @param response     api请求结果
     * @param fieldPath    字段路径
     * @param expectedValue 期望值
     * @return 字段值是否匹配
     */
    public static <T> Boolean jsonFieldEquals(ResponseEntity<T> response, String fieldPath, Object expectedValue) {
        if (!isValidJSON(response)) {
            return false;
        }

        try {
            String json = (String) response.getBody();
            JsonNode rootNode = objectMapper.readTree(json);

            String[] pathParts = fieldPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String part : pathParts) {
                if (currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    return false;
                }
            }

            // 比较值
            if (expectedValue == null) {
                return currentNode.isNull();
            } else if (expectedValue instanceof String) {
                return currentNode.asText().equals(expectedValue);
            } else if (expectedValue instanceof Integer) {
                return currentNode.asInt() == (Integer) expectedValue;
            } else if (expectedValue instanceof Long) {
                return currentNode.asLong() == (Long) expectedValue;
            } else if (expectedValue instanceof Double) {
                return Math.abs(currentNode.asDouble() - (Double) expectedValue) < 0.0001;
            } else if (expectedValue instanceof Boolean) {
                return currentNode.asBoolean() == (Boolean) expectedValue;
            } else {
                return currentNode.toString().equals(expectedValue.toString());
            }
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error comparing field " + fieldPath + ": " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 验证JSON数组的长度
     * @param response     api请求结果
     * @param arrayPath    数组字段路径
     * @param expectedSize 期望长度
     * @return 数组长度是否匹配
     */
    public static <T> Boolean jsonArrayHasSize(ResponseEntity<T> response, String arrayPath, int expectedSize) {
        if (!isValidJSON(response)) {
            return false;
        }

        try {
            String json = (String) response.getBody();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode arrayNode = rootNode;

            if (!arrayPath.isEmpty()) {
                String[] pathParts = arrayPath.split("\\.");
                for (String part : pathParts) {
                    if (arrayNode.has(part)) {
                        arrayNode = arrayNode.get(part);
                    } else {
                        return false;
                    }
                }
            }

            if (!arrayNode.isArray()) {
                if (MCUnitTests.getInstance().verbose) {
                    System.out.println("Field " + arrayPath + " is not an array");
                }
                return false;
            }

            return arrayNode.size() == expectedSize;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error checking array size: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 验证JSON数组是否不为空
     * @param response     api请求结果
     * @param arrayPath    数组字段路径（空字符串表示根节点就是数组）
     * @return 数组是否不为空
     */
    public static <T> Boolean jsonArrayNotEmpty(ResponseEntity<T> response, String arrayPath) {
        if (!isValidJSON(response)) {
            return false;
        }

        try {
            String json = (String) response.getBody();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode arrayNode = rootNode;

            if (arrayPath != null && !arrayPath.isEmpty()) {
                String[] pathParts = arrayPath.split("\\.");
                for (String part : pathParts) {
                    if (arrayNode.has(part)) {
                        arrayNode = arrayNode.get(part);
                    } else {
                        return false;
                    }
                }
            }

            return arrayNode.isArray() && arrayNode.size() > 0;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error checking array: " + e.getMessage());
            }
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
     * 验证URL的域名是否匹配预期
     * @param url          URL
     * @param expectedHost 期望的主机名
     * @return 域名是否匹配
     */
    public static Boolean urlHostMatches(String url, String expectedHost) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getHost().equals(expectedHost);
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error parsing URL: " + url);
            }
            return false;
        }
    }

    /**
     * 验证URL是否使用HTTPS协议
     * @param url URL
     * @return 是否使用HTTPS
     */
    public static Boolean isHttpsUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol().equals("https");
        } catch (Exception e) {
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

    /**
     * ContentType是否为text/html
     * @param response     api请求结果
     * @return Boolean 是否为text/html
     */
    public static <T> Boolean isHTMLContentType(ResponseEntity<T> response) {
        MediaType contentType = getMediaType(response);
        return contentType != null && MediaType.TEXT_HTML.includes(contentType);
    }

    /**
     * ContentType是否为application/xml
     * @param response     api请求结果
     * @return Boolean 是否为application/xml
     */
    public static <T> Boolean isXMLContentType(ResponseEntity<T> response) {
        MediaType contentType = getMediaType(response);
        return contentType != null &&
                (MediaType.APPLICATION_XML.includes(contentType) ||
                        MediaType.TEXT_XML.includes(contentType));
    }

    /**
     * ContentType是否为指定类型
     * @param response     api请求结果
     * @param expectedType 期望的MediaType
     * @return Boolean 是否匹配
     */
    public static <T> Boolean isContentType(ResponseEntity<T> response, MediaType expectedType) {
        MediaType contentType = getMediaType(response);
        return contentType != null && expectedType.includes(contentType);
    }

    /**
     * 验证响应状态码是否为2xx成功状态
     * @param result api响应结果
     * @return 是否为成功状态
     */
    public static Boolean isSuccessStatus(RequestUnitTestsResult result) {
        return result != null && result.statusCode >= 200 && result.statusCode < 300;
    }

    /**
     * 验证响应状态码是否为指定值
     * @param result       api响应结果
     * @param expectedCode 期望的状态码
     * @return 状态码是否匹配
     */
    public static Boolean isStatusCode(RequestUnitTestsResult result, int expectedCode) {
        return result != null && result.statusCode == expectedCode;
    }

    /**
     * 验证响应状态码是否为指定值
     * @param result         api响应结果
     * @param expectedStatus 期望的HttpStatus
     * @return 状态码是否匹配
     */
    public static Boolean isStatusCode(RequestUnitTestsResult result, HttpStatus expectedStatus) {
        return result != null && result.statusCode == expectedStatus.value();
    }

    /**
     * 验证响应状态码是否为4xx客户端错误
     * @param result api响应结果
     * @return 是否为客户端错误
     */
    public static Boolean isClientError(RequestUnitTestsResult result) {
        return result != null && result.statusCode >= 400 && result.statusCode < 500;
    }

    /**
     * 验证响应状态码是否为5xx服务器错误
     * @param result api响应结果
     * @return 是否为服务器错误
     */
    public static Boolean isServerError(RequestUnitTestsResult result) {
        return result != null && result.statusCode >= 500 && result.statusCode < 600;
    }

    /**
     * 验证响应头是否包含指定header
     * @param response   api请求结果
     * @param headerName header名称
     * @return 是否包含指定header
     */
    public static <T> Boolean hasHeader(ResponseEntity<T> response, String headerName) {
        return response != null &&
                response.getHeaders() != null &&
                response.getHeaders().containsKey(headerName);
    }

    /**
     * 验证响应头的值是否符合预期
     * @param response      api请求结果
     * @param headerName    header名称
     * @param expectedValue 期望值
     * @return header值是否匹配
     */
    public static <T> Boolean headerEquals(ResponseEntity<T> response, String headerName, String expectedValue) {
        if (!hasHeader(response, headerName)) {
            return false;
        }

        List<String> values = response.getHeaders().get(headerName);
        return values != null && values.contains(expectedValue);
    }

    /**
     * 验证响应体是否包含指定文本
     * @param response api请求结果
     * @param text     要查找的文本
     * @return 是否包含
     */
    public static <T> Boolean bodyContains(ResponseEntity<T> response, String text) {
        if (response == null || response.getBody() == null) {
            return false;
        }

        String body = response.getBody().toString();
        return body.contains(text);
    }

    /**
     * 验证响应体是否匹配正则表达式
     * @param response api请求结果
     * @param regex    正则表达式
     * @return 是否匹配
     */
    public static <T> Boolean bodyMatchesRegex(ResponseEntity<T> response, String regex) {
        if (response == null || response.getBody() == null) {
            return false;
        }

        String body = response.getBody().toString();
        return Pattern.compile(regex).matcher(body).find();
    }

    /**
     * 验证响应体是否为空
     * @param response api请求结果
     * @return 是否为空
     */
    public static <T> Boolean isBodyEmpty(ResponseEntity<T> response) {
        if (response == null || response.getBody() == null) {
            return true;
        }

        String body = response.getBody().toString();
        return body == null || body.trim().isEmpty();
    }

    /**
     * 验证响应体是否不为空
     * @param response api请求结果
     * @return 是否不为空
     */
    public static <T> Boolean isBodyNotEmpty(ResponseEntity<T> response) {
        return !isBodyEmpty(response);
    }

    /**
     * 获取响应体大小（字节）
     * @param response api请求结果
     * @return 响应体大小
     */
    public static <T> int getBodySize(ResponseEntity<T> response) {
        if (response == null || response.getBody() == null) {
            return 0;
        }

        String body = response.getBody().toString();
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 验证响应体大小是否在指定范围内
     * @param response api请求结果
     * @param minSize  最小字节数（-1表示不限制）
     * @param maxSize  最大字节数（-1表示不限制）
     * @return 大小是否在范围内
     */
    public static <T> Boolean isBodySizeInRange(ResponseEntity<T> response, int minSize, int maxSize) {
        int bodySize = getBodySize(response);

        if (minSize >= 0 && bodySize < minSize) {
            return false;
        }

        if (maxSize >= 0 && bodySize > maxSize) {
            return false;
        }

        return true;
    }

    /**
     * 验证响应体是否为有效的XML
     * @param response api请求结果
     * @return 是否为有效XML
     */
    public static <T> Boolean isValidXML(ResponseEntity<T> response) {
        if (response == null || response.getBody() == null) {
            return false;
        }

        String body = response.getBody().toString();
        try {
            javax.xml.parsers.DocumentBuilderFactory factory =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new java.io.ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
            return true;
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Invalid XML: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 验证响应编码是否为UTF-8
     * @param response api请求结果
     * @return 是否为UTF-8编码
     */
    public static <T> Boolean isUTF8Encoding(ResponseEntity<T> response) {
        MediaType contentType = getMediaType(response);
        if (contentType == null || contentType.getCharset() == null) {
            return false;
        }

        return StandardCharsets.UTF_8.equals(contentType.getCharset());
    }

    /**
     * 比较两个响应是否相同
     * @param response1 第一个响应
     * @param response2 第二个响应
     * @return 是否相同
     */
    public static <T> Boolean responsesEqual(ResponseEntity<T> response1, ResponseEntity<T> response2) {
        if (response1 == null || response2 == null) {
            return response1 == response2;
        }

        // 比较状态码
        if (!response1.getStatusCode().equals(response2.getStatusCode())) {
            return false;
        }

        // 比较响应体
        String body1 = response1.getBody() != null ? response1.getBody().toString() : "";
        String body2 = response2.getBody() != null ? response2.getBody().toString() : "";

        return body1.equals(body2);
    }

    /**
     * 验证响应时间是否稳定（多次请求的响应时间波动不大）
     * @param responseTimes 多次请求的响应时间列表
     * @param maxDeviation  最大允许偏差比例（例如0.2表示20%）
     * @return 是否稳定
     */
    public static Boolean isResponseTimeStable(List<Long> responseTimes, double maxDeviation) {
        if (responseTimes == null || responseTimes.size() < 2) {
            return true;
        }

        // 计算平均值
        double avg = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        // 检查每个值是否在允许范围内
        for (Long time : responseTimes) {
            double deviation = Math.abs(time - avg) / avg;
            if (deviation > maxDeviation) {
                if (MCUnitTests.getInstance().verbose) {
                    System.out.println("Response time " + time + "ms deviates " +
                            String.format("%.2f%%", deviation * 100) +
                            " from average " + String.format("%.2f", avg) + "ms");
                }
                return false;
            }
        }

        return true;
    }

    /**
     * 计算响应时间列表的标准差
     * @param responseTimes 响应时间列表
     * @return 标准差
     */
    public static double calculateStandardDeviation(List<Long> responseTimes) {
        if (responseTimes == null || responseTimes.isEmpty()) {
            return 0.0;
        }

        double avg = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double variance = responseTimes.stream()
                .mapToDouble(time -> Math.pow(time - avg, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * 获取响应时间的百分位数
     * @param responseTimes 响应时间列表
     * @param percentile    百分位（例如95表示P95）
     * @return 百分位值
     */
    public static long getPercentile(List<Long> responseTimes, int percentile) {
        if (responseTimes == null || responseTimes.isEmpty()) {
            return 0;
        }

        List<Long> sorted = new ArrayList<>(responseTimes);
        Collections.sort(sorted);

        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));

        return sorted.get(index);
    }

    /**
     * 验证响应是否包含分页信息
     * @param response api请求结果
     * @param pageField 页码字段名
     * @param sizeField 每页大小字段名
     * @param totalField 总数字段名
     * @return 是否包含完整的分页信息
     */
    public static <T> Boolean hasPaginationInfo(ResponseEntity<T> response,
                                                String pageField,
                                                String sizeField,
                                                String totalField) {
        if (!isValidJSON(response)) {
            return false;
        }

        return jsonContainsField(response, pageField) &&
                jsonContainsField(response, sizeField) &&
                jsonContainsField(response, totalField);
    }

    /**
     * 验证JSON响应是否符合预期的schema结构
     * @param response       api请求结果
     * @param requiredFields 必需字段列表
     * @return 是否包含所有必需字段
     */
    public static <T> Boolean jsonHasRequiredFields(ResponseEntity<T> response, List<String> requiredFields) {
        if (!isValidJSON(response)) {
            return false;
        }

        for (String field : requiredFields) {
            if (!jsonContainsField(response, field)) {
                if (MCUnitTests.getInstance().verbose) {
                    System.out.println("Missing required field: " + field);
                }
                return false;
            }
        }

        return true;
    }

    /**
     * 验证响应是否包含错误信息
     * @param response       api请求结果
     * @param errorFieldName 错误字段名（如"error", "message", "errorMessage"）
     * @return 是否包含错误信息
     */
    public static <T> Boolean hasErrorMessage(ResponseEntity<T> response, String errorFieldName) {
        if (!isValidJSON(response)) {
            return bodyContains(response, "error") || bodyContains(response, "Error");
        }

        return jsonContainsField(response, errorFieldName);
    }

    /**
     * 提取JSON字段的值
     * @param response  api请求结果
     * @param fieldPath 字段路径
     * @return 字段值（字符串形式）
     */
    public static <T> String extractJsonFieldValue(ResponseEntity<T> response, String fieldPath) {
        if (!isValidJSON(response)) {
            return null;
        }

        try {
            String json = (String) response.getBody();
            JsonNode rootNode = objectMapper.readTree(json);

            String[] pathParts = fieldPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String part : pathParts) {
                if (currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    return null;
                }
            }

            return currentNode.asText();
        } catch (Exception e) {
            if (MCUnitTests.getInstance().verbose) {
                System.out.println("Error extracting field " + fieldPath + ": " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * 验证响应缓存header是否正确设置
     * @param response api请求结果
     * @return 是否设置了缓存控制
     */
    public static <T> Boolean hasCacheControl(ResponseEntity<T> response) {
        return hasHeader(response, "Cache-Control") || hasHeader(response, "Expires");
    }

    /**
     * 验证响应是否允许跨域（CORS）
     * @param response api请求结果
     * @return 是否设置了CORS header
     */
    public static <T> Boolean allowsCORS(ResponseEntity<T> response) {
        return hasHeader(response, "Access-Control-Allow-Origin");
    }

    /**
     * 数据验证结果类 - 用于复杂验证场景
     */
    public static class ValidationResult {
        public final boolean isValid;
        public final String message;
        public final Map<String, Object> details;

        public ValidationResult(boolean isValid, String message) {
            this(isValid, message, new HashMap<>());
        }

        public ValidationResult(boolean isValid, String message, Map<String, Object> details) {
            this.isValid = isValid;
            this.message = message;
            this.details = details;
        }

        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s', details=%s}",
                    isValid, message, details);
        }
    }

    /**
     * 综合验证响应
     * @param response api请求结果
     * @param expectedStatus 期望的状态码
     * @param expectedContentType 期望的ContentType
     * @param requiredFields 必需的JSON字段（可为null）
     * @return 验证结果
     */
    public static <T> ValidationResult validateResponse(ResponseEntity<T> response,
                                                        HttpStatus expectedStatus,
                                                        MediaType expectedContentType,
                                                        List<String> requiredFields) {
        Map<String, Object> details = new HashMap<>();

        // 验证状态码
        if (!response.getStatusCode().equals(expectedStatus)) {
            details.put("expectedStatus", expectedStatus.value());
            details.put("actualStatus", response.getStatusCode().value());
            return new ValidationResult(false, "Status code mismatch", details);
        }

        // 验证Content-Type
        if (expectedContentType != null && !isContentType(response, expectedContentType)) {
            details.put("expectedContentType", expectedContentType.toString());
            details.put("actualContentType", getMediaType(response).toString());
            return new ValidationResult(false, "Content-Type mismatch", details);
        }

        // 验证必需字段
        if (requiredFields != null && !requiredFields.isEmpty()) {
            if (!jsonHasRequiredFields(response, requiredFields)) {
                details.put("requiredFields", requiredFields);
                return new ValidationResult(false, "Missing required fields", details);
            }
        }

        return new ValidationResult(true, "All validations passed", details);
    }
}