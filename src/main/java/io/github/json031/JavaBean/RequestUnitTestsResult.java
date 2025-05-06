package io.github.json031.JavaBean;

import org.springframework.http.ResponseEntity;

import java.time.Instant;

/**
 * Request Unit Tests Result.
 */
public class RequestUnitTestsResult {
    //请求发起时间与结束时间（用于更精确分析请求波峰）
    public final Instant startTime;
    public final Instant endTime;
    //错误信息（Error Details）
    public final String errorMessage;

    //是否请求成功（Success Boolean）
    public final boolean isSuccess;

    //请求状态码（HTTP Status Code）
    public final int statusCode;
    //请求 URL（用于动态构造或多 API 测试时溯源）
    public final String requestUrl;
    //请求方法（GET / POST / PUT / DELETE 等）
    public final String method;
    //线程 ID（并发测试中识别发起线程）
    public final long threadId;

    // 毫秒级耗时
    public final long durationMillis;
    // api请求响应结果
    public final ResponseEntity<String> response;
    // 响应大小（字节）
    public final int responseSizeBytes;

    public RequestUnitTestsResult(
            long durationMillis,
            ResponseEntity<String> response,
            int statusCode,
            boolean isSuccess,
            String errorMessage,
            String requestUrl,
            String method,
            int responseSizeBytes,
            Instant startTime,
            Instant endTime,
            long threadId
    ) {
        this.durationMillis = durationMillis;
        this.response = response;
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.requestUrl = requestUrl;
        this.method = method;
        this.startTime = startTime;
        this.endTime = endTime;
        this.responseSizeBytes = responseSizeBytes;
        this.threadId = threadId;
    }

    public static RequestUnitTestsResult testSuccessResult(
            long durationMillis,
            ResponseEntity<String> response,
            String requestUrl,
            String method
    ) {
        RequestUnitTestsResult testSuccessResult = new RequestUnitTestsResult(
                durationMillis,
                response,
                200,
                true,
                "",
                requestUrl,
                method,
                0,
                Instant.now(),
                Instant.now(),
                0);
        return testSuccessResult;
    }
}
